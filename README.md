# localstack-error

This project demonstrates 2 errors with localstack.

The first error involves doing many puts in parallel:

*Note: This error has been fixed by https://github.com/localstack/localstack/pull/1217*

1. Clone this repo
1. Make sure Docker is running on your machine
1. Run `mvn clean install -Dtest=LocalstackTest#testParallelWrites`
1. See ~~many errors (`Unable to verify integrity of data upload`)~~ no errors
1. Change `MAX_THREADS` to 1 in `LocalstackTest`
1. Run `mvn clean install -Dtest=LocalstackTest#testParallelWrites` again
1. See no errors
1. Be ~~confused and sad~~ happy


The second error involves doing a put with data that has a trailing newline:

1. Clone this repo
1. Make sure Docker is running on your machine
1. Run `mvn clean install -Dtest=LocalstackTest#testTrailingNewline`
1. See an error (`Unable to verify integrity of data upload`)
1. Remove the trailing newline in the `contents` variable in the `testTrailingNewline` method in `LocalstackTest`
1. Run `mvn clean install -Dtest=LocalstackTest#testTrailingNewline` again
1. Be confused and sad
