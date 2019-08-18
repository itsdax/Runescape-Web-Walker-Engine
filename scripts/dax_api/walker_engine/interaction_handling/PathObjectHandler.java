package scripts.dax_api.walker_engine.interaction_handling;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.tribot.api.General;
import org.tribot.api2007.Objects;
import org.tribot.api2007.Player;
import org.tribot.api2007.ext.Filters;
import org.tribot.api2007.types.RSArea;
import org.tribot.api2007.types.RSObject;
import org.tribot.api2007.types.RSObjectDefinition;
import org.tribot.api2007.types.RSTile;
import scripts.dax_api.shared.helpers.RSObjectHelper;
import scripts.dax_api.walker.utils.AccurateMouse;
import scripts.dax_api.walker_engine.Loggable;
import scripts.dax_api.walker_engine.WaitFor;
import scripts.dax_api.walker_engine.WalkerEngine;
import scripts.dax_api.walker_engine.bfs.BFS;
import scripts.dax_api.walker_engine.local_pathfinding.PathAnalyzer;
import scripts.dax_api.walker_engine.local_pathfinding.Reachable;
import scripts.dax_api.walker_engine.real_time_collision.RealTimeCollisionTile;

public class PathObjectHandler implements Loggable
{
	private static final Set<String> DOOR_NAMES = ImmutableSet.of("Gate of War", "Rickety door", "Oozing barrier", "Portal of Death");
	private static PathObjectHandler instance;
	private final Set<String> sortedOptions, sortedBlackList, sortedBlackListOptions, sortedHighPriorityOptions;

	private PathObjectHandler()
	{
		sortedOptions = ImmutableSet.of("Enter", "Cross", "Pass", "Open", "Close", "Walk-through", "Use", "Pass-through", "Exit",
			"Walk-Across", "Go-through", "Walk-across", "Climb", "Climb-up", "Climb-down", "Climb-over", "Climb over", "Climb-into", "Climb-through",
			"Board", "Jump-from", "Jump-across", "Jump-to", "Squeeze-through", "Jump-over", "Pay-toll(10gp)", "Step-over", "Walk-down", "Walk-up", "Travel", "Get in",
			"Investigate", "Operate"
		);
		sortedBlackList = ImmutableSet.of("Coffin", "null", "Board", "Jump-from", "Jump-across", "Jump-to", "Squeeze-through",
			"Jump-over", "Pay-toll(10gp)", "Step-over", "Walk-down", "Walk-up", "Walk-Up", "Travel", "Get in", "Investigate", "Operate", "Climb-under", "Jump", "Drawers"
		);
		sortedBlackListOptions = ImmutableSet.of("Chop down");
		sortedHighPriorityOptions = ImmutableSet.of("Pay-toll(10gp)");
	}

	private static PathObjectHandler getInstance()
	{
		return instance != null ? instance : (instance = new PathObjectHandler());
	}

	private enum SpecialObject
	{
		WEB("Web", "Slash", null, new SpecialCondition()
		{
			@Override
			boolean isSpecialLocation(PathAnalyzer.DestinationDetails destinationDetails)
			{
				return Objects.find(15, Filters.Objects.inArea(new RSArea(destinationDetails.getAssumed(), 1))
					.and(Filters.Objects.nameEquals("Web"))
					.and(Filters.Objects.actionsContains("Slash"))).length > 0;
			}
		}),
		ROCKFALL("Rockfall", "Mine", null, new SpecialCondition()
		{
			@Override
			boolean isSpecialLocation(PathAnalyzer.DestinationDetails destinationDetails)
			{
				return Objects.find(15, Filters.Objects.inArea(new RSArea(destinationDetails.getAssumed(), 1))
					.and(Filters.Objects.nameEquals("Rockfall"))
					.and(Filters.Objects.actionsContains("Mine"))).length > 0;
			}
		}),
		ROOTS("Roots", "Chop", null, new SpecialCondition()
		{
			@Override
			boolean isSpecialLocation(PathAnalyzer.DestinationDetails destinationDetails)
			{
				return Objects.find(15, Filters.Objects.inArea(new RSArea(destinationDetails.getAssumed(), 1))
					.and(Filters.Objects.nameEquals("Roots"))
					.and(Filters.Objects.actionsContains("Chop"))).length > 0;
			}
		}),
		ROCK_SLIDE("Rockslide", "Climb-over", null, new SpecialCondition()
		{
			@Override
			boolean isSpecialLocation(PathAnalyzer.DestinationDetails destinationDetails)
			{
				return Objects.find(15, Filters.Objects.inArea(new RSArea(destinationDetails.getAssumed(), 1))
					.and(Filters.Objects.nameEquals("Rockslide"))
					.and(Filters.Objects.actionsContains("Climb-over"))).length > 0;
			}
		}),
		ROOT("Root", "Step-over", null, new SpecialCondition()
		{
			@Override
			boolean isSpecialLocation(PathAnalyzer.DestinationDetails destinationDetails)
			{
				return Objects.find(15, Filters.Objects.inArea(new RSArea(destinationDetails.getAssumed(), 1))
					.and(Filters.Objects.nameEquals("Root"))
					.and(Filters.Objects.actionsContains("Step-over"))).length > 0;
			}
		}),
		BRIMHAVEN_VINES("Vines", "Chop-down", null, new SpecialCondition()
		{
			@Override
			boolean isSpecialLocation(PathAnalyzer.DestinationDetails destinationDetails)
			{
				return Objects.find(15, Filters.Objects.inArea(new RSArea(destinationDetails.getAssumed(), 1))
					.and(Filters.Objects.nameEquals("Vines"))
					.and(Filters.Objects.actionsContains("Chop-down"))).length > 0;
			}
		}),
		AVA_BOOKCASE("Bookcase", "Search", new RSTile(3097, 3359, 0), new SpecialCondition()
		{
			@Override
			boolean isSpecialLocation(PathAnalyzer.DestinationDetails destinationDetails)
			{
				return destinationDetails.getDestination().getX() >= 3097 && destinationDetails.getAssumed().equals(new RSTile(3097, 3359, 0));
			}
		}),
		AVA_LEVER("Lever", "Pull", new RSTile(3096, 3357, 0), new SpecialCondition()
		{
			@Override
			boolean isSpecialLocation(PathAnalyzer.DestinationDetails destinationDetails)
			{
				return destinationDetails.getDestination().getX() < 3097 && destinationDetails.getAssumed().equals(new RSTile(3097, 3359, 0));
			}
		}),
		ARDY_DOOR_LOCK_SIDE("Door", "Pick-lock", new RSTile(2565, 3356, 0), new SpecialCondition()
		{
			@Override
			boolean isSpecialLocation(PathAnalyzer.DestinationDetails destinationDetails)
			{
				return Player.getPosition().getX() >= 2565 && Player.getPosition().distanceTo(new RSTile(2565, 3356, 0)) < 3;
			}
		}),
		ARDY_DOOR_UNLOCKED_SIDE("Door", "Open", new RSTile(2565, 3356, 0), new SpecialCondition()
		{
			@Override
			boolean isSpecialLocation(PathAnalyzer.DestinationDetails destinationDetails)
			{
				return Player.getPosition().getX() < 2565 && Player.getPosition().distanceTo(new RSTile(2565, 3356, 0)) < 3;
			}
		}),
		YANILLE_DOOR_LOCK_SIDE("Door", "Pick-lock", new RSTile(2601, 9482, 0), new SpecialCondition()
		{
			@Override
			boolean isSpecialLocation(PathAnalyzer.DestinationDetails destinationDetails)
			{
				return Player.getPosition().getY() <= 9481;
			}
		}),
		YANILLE_DOOR_UNLOCKED_SIDE("Door", "Open", new RSTile(2601, 9482, 0), new SpecialCondition()
		{
			@Override
			boolean isSpecialLocation(PathAnalyzer.DestinationDetails destinationDetails)
			{
				return Player.getPosition().getY() > 9481;
			}
		});

		private String name, action;
		private RSTile location;
		private SpecialCondition specialCondition;

		SpecialObject(String name, String action, RSTile location, SpecialCondition specialCondition)
		{
			this.name = name;
			this.action = action;
			this.location = location;
			this.specialCondition = specialCondition;
		}

		public String getName()
		{
			return name;
		}

		public String getAction()
		{
			return action;
		}

		public RSTile getLocation()
		{
			return location;
		}

		public boolean isSpecialCondition(PathAnalyzer.DestinationDetails destinationDetails)
		{
			return specialCondition.isSpecialLocation(destinationDetails);
		}

		public static SpecialObject getValidSpecialObjects(PathAnalyzer.DestinationDetails destinationDetails)
		{
			for (SpecialObject object : values())
			{
				if (object.isSpecialCondition(destinationDetails))
				{
					return object;
				}
			}
			return null;
		}
	}

	private abstract static class SpecialCondition
	{
		abstract boolean isSpecialLocation(PathAnalyzer.DestinationDetails destinationDetails);
	}

	public static boolean handle(PathAnalyzer.DestinationDetails destinationDetails, ArrayList<RSTile> path)
	{
		final RealTimeCollisionTile start = destinationDetails.getDestination(), end = destinationDetails.getNextTile();
		final SpecialObject specialObject = SpecialObject.getValidSpecialObjects(destinationDetails);

		RSObject[] interactiveObjects;
		String action = null;

		if (specialObject == null)
		{
			if ((interactiveObjects = getInteractiveObjects(start.getX(), start.getY(), start.getZ(), destinationDetails)).length < 1 && end != null)
			{
				interactiveObjects = getInteractiveObjects(end.getX(), end.getY(), end.getZ(), destinationDetails);
			}
		}
		else
		{
			action = specialObject.getAction();
			interactiveObjects = specialObjectFilter(specialObject, destinationDetails);
		}

		if (interactiveObjects.length == 0)
		{
			return false;
		}

		final StringBuilder stringBuilder = new StringBuilder("Sort Order: ");
		Arrays.stream(interactiveObjects).forEach(rsObject -> stringBuilder
			.append(rsObject.getDefinition().getName())
			.append(" ")
			.append(ImmutableList.copyOf(rsObject.getDefinition().getActions())).append(", ")
		);
		getInstance().log(stringBuilder);

		return handle(path, interactiveObjects[0], destinationDetails, action, specialObject);
	}

	private static boolean handle(ArrayList<RSTile> path, RSObject object, PathAnalyzer.DestinationDetails destinationDetails, String action, SpecialObject specialObject)
	{
		final PathAnalyzer.DestinationDetails current = PathAnalyzer.furthestReachableTile(path);

		if (current == null)
		{
			return false;
		}

		final RealTimeCollisionTile currentFurthest = current.getDestination();

		if (!Player.isMoving() && (!object.isOnScreen() || !object.isClickable()))
		{
			if (!WalkerEngine.getInstance().clickMinimap(destinationDetails.getDestination()))
			{
				return false;
			}
		}

		if (WaitFor.condition(General.random(5000, 8000), () -> object.isOnScreen() && object.isClickable() ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE) != WaitFor.Return.SUCCESS)
		{
			return false;
		}

		boolean successfulClick = false;

		if (specialObject != null)
		{
			getInstance().log("Detected Special Object: " + specialObject);
			switch (specialObject)
			{
				case WEB:
					List<RSObject> webs;
					int iterations = 0;
					while ((webs = Arrays.stream(Objects.getAt(object.getPosition())).filter(object1 -> ImmutableList.copyOf(RSObjectHelper.getActions(object1)).contains("Slash")).collect(Collectors.toList())).size() > 0)
					{
						final RSObject web = webs.get(0);
						InteractionHelper.click(web, "Slash");
						if (web.getPosition().distanceTo(Player.getPosition()) <= 1)
						{
							WaitFor.milliseconds(General.randomSD(50, 800, 250, 150));
						}
						else
						{
							WaitFor.milliseconds(2000, 4000);
						}
						if (Reachable.getMap().getParent(destinationDetails.getAssumedX(), destinationDetails.getAssumedY()) != null)
						{
							successfulClick = true;
							break;
						}
						if (iterations++ > 5)
						{
							break;
						}
					}
					break;
				case ARDY_DOOR_LOCK_SIDE:
					for (int i = 0; i < General.random(15, 25); i++)
					{
						if (!clickOnObject(object, new String[]{specialObject.getAction()}))
						{
							continue;
						}
						if (Player.getPosition().distanceTo(specialObject.getLocation()) > 1)
						{
							WaitFor.condition(General.random(3000, 4000), () -> Player.getPosition().distanceTo(specialObject.getLocation()) <= 1 ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE);
						}
						if (Player.getPosition().equals(new RSTile(2564, 3356, 0)))
						{
							successfulClick = true;
							break;
						}
					}
					break;
			}
		}

		if (!successfulClick)
		{
			final String[] validOptions = action != null ? new String[]{action} :
				getViableOption(Arrays.stream(object.getDefinition().getActions())
					.filter(getInstance().sortedOptions::contains)
					.collect(Collectors.toList()), destinationDetails
				);
			if (!clickOnObject(object, validOptions))
			{
				return false;
			}
		}

		final boolean strongholdDoor = isStrongholdDoor(object);

		if (strongholdDoor)
		{
			if (WaitFor.condition(General.random(6700, 7800), () -> {
				final RSTile playerPosition = Player.getPosition();
				if (BFS.isReachable(RealTimeCollisionTile.get(playerPosition.getX(), playerPosition.getY(), playerPosition.getPlane()), destinationDetails.getNextTile(), 50))
				{
					WaitFor.milliseconds(500, 1000);
					return WaitFor.Return.SUCCESS;
				}
				if (NPCInteraction.isConversationWindowUp())
				{
					handleStrongholdQuestions();
					return WaitFor.Return.SUCCESS;
				}
				return WaitFor.Return.IGNORE;
			}) != WaitFor.Return.SUCCESS)
			{
				return false;
			}
		}

		WaitFor.condition(General.random(8500, 11000), () -> {
			DoomsToggle.handleToggle();
			final PathAnalyzer.DestinationDetails destinationDetails1 = PathAnalyzer.furthestReachableTile(path);
			if (NPCInteraction.isConversationWindowUp())
			{
				NPCInteraction.handleConversation(NPCInteraction.GENERAL_RESPONSES);
			}
			if (destinationDetails1 != null)
			{
				if (!destinationDetails1.getDestination().equals(currentFurthest))
				{
					return WaitFor.Return.SUCCESS;
				}
			}
			if (current.getNextTile() != null)
			{
				final PathAnalyzer.DestinationDetails hoverDetails = PathAnalyzer.furthestReachableTile(path, current.getNextTile());
				if (hoverDetails != null && hoverDetails.getDestination() != null && hoverDetails.getDestination().getRSTile().distanceTo(Player.getPosition()) > 7 && !strongholdDoor && Player.getPosition().distanceTo(object) <= 2)
				{
					WalkerEngine.getInstance().hoverMinimap(hoverDetails.getDestination());
				}
			}
			return WaitFor.Return.IGNORE;
		});
		if (strongholdDoor)
		{
			General.sleep(800, 1200);
		}
		return true;
	}

	public static RSObject[] getInteractiveObjects(int x, int y, int z, PathAnalyzer.DestinationDetails destinationDetails)
	{
		final RSObject[] objects = interactiveObjectFilter(x, y, z, destinationDetails);
		final RSTile base = new RSTile(x, y, z);
		Arrays.sort(objects, (o1, o2) -> {
			int c = Integer.compare(o1.getPosition().distanceTo(base), o2.getPosition().distanceTo(base));
			int assumedZ = destinationDetails.getAssumedZ(), destinationZ = destinationDetails.getDestination().getZ();
			List<String> actions1 = ImmutableList.copyOf(o1.getDefinition().getActions());
			List<String> actions2 = ImmutableList.copyOf(o2.getDefinition().getActions());

			if (assumedZ > destinationZ)
			{
				if (actions1.contains("Climb-up"))
				{
					return -1;
				}
				if (actions2.contains("Climb-up"))
				{
					return 1;
				}
			}

			if (assumedZ < destinationZ)
			{
				if (actions1.contains("Climb-down"))
				{
					return -1;
				}
				if (actions2.contains("Climb-down"))
				{
					return 1;
				}
			}
			return c;
		});
		final StringBuilder builder = new StringBuilder("Detected: ");
		Arrays.stream(objects).forEach(object ->
			builder.append(object.getDefinition().getName()).append(" ")
		);
		getInstance().log(builder);
		return objects;
	}

	/**
	 * Filter that accepts only interactive objects to progress in path.
	 *
	 * @param x                  x-co-ord
	 * @param y                  y-co-ord
	 * @param z                  z-co-ord
	 * @param destinationDetails context where destination is at
	 */
	private static RSObject[] interactiveObjectFilter(int x, int y, int z, PathAnalyzer.DestinationDetails destinationDetails)
	{
		final RSTile position = new RSTile(x, y, z);
		final Predicate<RSObject> objectPredicate = (obj) ->
		{
			final RSObjectDefinition def = obj.getDefinition();
			if (def == null)
			{
				return false;
			}
			final String name = def.getName();
			if (getInstance().sortedBlackList.contains(name))
			{
				return false;
			}
			if (RSObjectHelper.getActionsList(obj).stream().anyMatch(s -> getInstance().sortedBlackListOptions.contains(s)))
			{
				return false;
			}
			if (obj.getPosition().distanceTo(destinationDetails.getDestination().getRSTile()) > 5)
			{
				return false;
			}
			if (Arrays.stream(obj.getAllTiles()).noneMatch(rsTile -> rsTile.distanceTo(position) <= 2))
			{
				return false;
			}
			final List<String> options = ImmutableList.copyOf(def.getActions());
			return options.stream().anyMatch(getInstance().sortedOptions::contains);
		};
		return Objects.getAll(25, objectPredicate);
	}

	private static String[] getViableOption(Collection<String> collection, PathAnalyzer.DestinationDetails destinationDetails)
	{
		final Set<String> set = ImmutableSet.copyOf(collection);

		if (set.retainAll(getInstance().sortedHighPriorityOptions) && set.size() > 0)
		{
			return set.toArray(new String[0]);
		}
		if (destinationDetails.getAssumedZ() > destinationDetails.getDestination().getZ())
		{
			if (collection.contains("Climb-up"))
			{
				return new String[]{"Climb-up"};
			}
		}
		if (destinationDetails.getAssumedZ() < destinationDetails.getDestination().getZ())
		{
			if (collection.contains("Climb-down"))
			{
				return new String[]{"Climb-down"};
			}
		}
		if (destinationDetails.getAssumedY() > 5000 && destinationDetails.getDestination().getZ() == 0 && destinationDetails.getAssumedZ() == 0)
		{
			if (collection.contains("Climb-down"))
			{
				return new String[]{"Climb-down"};
			}
		}
		final String[] options = new String[collection.size()];
		collection.toArray(options);
		return options;
	}

	private static boolean clickOnObject(RSObject object, String[] options)
	{
		boolean result;

		if (isClosedTrapDoor(object, options))
		{
			result = handleTrapDoor(object);
		}
		else
		{
			result = AccurateMouse.click(object, options);
			getInstance().log("Interacting with (" + RSObjectHelper.getName(object) + ") at " + object.getPosition() + " with options: " + Arrays.toString(options) + " " + (result ? "SUCCESS" : "FAIL"));
		}

		return result;
	}

	private static boolean isStrongholdDoor(RSObject object)
	{
		return DOOR_NAMES.contains(object.getDefinition().getName());
	}

	private static void handleStrongholdQuestions()
	{
		NPCInteraction.handleConversation("Use the Account Recovery System.",
			"Nobody.",
			"Don't tell them anything and click the 'Report Abuse' button.",
			"Me.",
			"Only on the RuneScape website.",
			"Report the incident and do not click any links.",
			"Authenticator and two-step login on my registered email.",
			"No way! You'll just take my gold for your own! Reported!",
			"No.",
			"Don't give them the information and send an 'Abuse Report'.",
			"Don't give them my password.",
			"The birthday of a famous person or event.",
			"Through account settings on runescape.com.",
			"Secure my device and reset my RuneScape password.",
			"Report the player for phishing.",
			"Don't click any links, forward the email to reportphishing@jagex.com.",
			"Inform Jagex by emailing reportphishing@jagex.com.",
			"Don't give out your password to anyone. Not even close friends.",
			"Politely tell them no and then use the 'Report Abuse' button.",
			"Set up 2 step authentication with my email provider.",
			"No, you should never buy a RuneScape account.",
			"Do not visit the website and report the player who messaged you.",
			"Only on the RuneScape website.",
			"Don't type in my password backwards and report the player.",
			"Virus scan my device then change my password.",
			"No, you should never allow anyone to level your account.",
			"Don't give out your password to anyone. Not even close friends.",
			"Report the stream as a scam. Real Jagex streams have a 'verified' mark.",
			"Read the text and follow the advice given.",
			"No way! I'm reporting you to Jagex!",
			"Talk to any banker in RuneScape.",
			"Secure my device and reset my RuneScape password.",
			"Don't share your information and report the player.");
	}

	private static boolean isClosedTrapDoor(RSObject object, String[] options)
	{
		return (object.getDefinition().getName().equals("Trapdoor") && ImmutableList.copyOf(options).contains("Open"));
	}

	private static boolean handleTrapDoor(RSObject object)
	{
		if (getActions(object).contains("Open"))
		{
			if (!InteractionHelper.click(object, "Open", () -> {
				final RSObject[] objects = Objects.find(15, Filters.Objects.actionsContains("Climb-down")
					.and(Filters.Objects.inArea(new RSArea(object, 2)))
				);
				if (objects.length > 0 && getActions(objects[0]).contains("Climb-down"))
				{
					return WaitFor.Return.SUCCESS;
				}
				return WaitFor.Return.IGNORE;
			}))
			{
				return false;
			}
			else
			{
				final RSObject[] objects = Objects.find(15, Filters.Objects.actionsContains("Climb-down")
					.and(Filters.Objects.inArea(new RSArea(object, 2)))
				);
				return objects.length > 0 && handleTrapDoor(objects[0]);
			}
		}
		getInstance().log("Interacting with (" + object.getDefinition().getName() + ") at " + object.getPosition() + " with option: Climb-down");
		return AccurateMouse.click(object, "Climb-down");
	}

	private static RSObject[] specialObjectFilter(SpecialObject specialObject, PathAnalyzer.DestinationDetails destinationDetails)
	{
		final Predicate<RSObject> filter = (i) ->
		{
			final List<String> tmp = ImmutableList.copyOf(i.getDefinition().getActions());
			final RSArea tmpArea = new RSArea(specialObject.getLocation() != null ? specialObject.getLocation() : destinationDetails.getAssumed(), 1);

			if (i.getDefinition().getName() == null || tmp.size() <= 0)
			{
				return false;
			}

			return tmp.contains(specialObject.getAction()) && tmpArea.contains(i) && i.getDefinition().getName().equals(specialObject.getName());
		};
		return Objects.findNearest(15, filter);
	}

	public static List<String> getActions(RSObject object)
	{
		final List<String> list = new ArrayList<>();
		if (object == null)
		{
			return list;
		}
		final RSObjectDefinition objectDefinition = object.getDefinition();
		if (objectDefinition == null)
		{
			return list;
		}
		final String[] actions = objectDefinition.getActions();
		if (actions == null)
		{
			return list;
		}
		return ImmutableList.copyOf(actions);
	}

	@Override
	public String getName()
	{
		return "Object Handler";
	}
}
