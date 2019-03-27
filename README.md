# localstack-error

1. Clone this repo
1. Make sure Docker is running on your machine
1. Run `mvn clean install`
1. See many errors (`Unable to execute HTTP request: The target server failed to respond`)
1. Change `MAX_THREADS` to 1 in `LocalstackTest`
1. Run `mvn clean install` again
1. See no errors
1. Be confused and sad