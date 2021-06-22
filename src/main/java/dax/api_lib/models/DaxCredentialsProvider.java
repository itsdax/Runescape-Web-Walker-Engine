package dax.api_lib.models;

@FunctionalInterface
public interface DaxCredentialsProvider {
    DaxCredentials getDaxCredentials();
}
