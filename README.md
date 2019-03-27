# localstack-error

1. Clone this repo
1. Make sure Docker is running on your machine
1. Run `mvn clean install`
1. See the failures
1. Change `MAX_THREADS` to 1 in `LocalstackTest`
1. Run `mvn clean install` again
1. See no failures
1. Be confused and sad