package scripts.dax_api.api_lib.models;

@FunctionalInterface
public interface DaxCredentialsProvider {
    DaxCredentials getDaxCredentials();
}
