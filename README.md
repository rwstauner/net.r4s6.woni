# woni

Your data: Was Outside, Now Inside.


## Usage

This project uses the latest `clj` tool (1.10.2) with `deps.edn`.

### CLI

For usage info:

    $ clojure -M:run --help

or you can use `bin/cli ...`.
Run the cli passing in input files and optional sort fields:

    $ clojure -M:run --sort fields,to,sort input.csv...
    record1
    record2

You can specify the paths to multiple input files on the command line.
A dash indicates STDIN.

The `--sort` parameter is a comma-separated list of field names
each of which can be prefixed with a dash to indicate descending.

To sort by email descending then last name ascending:

    $ clojure -M:run --sort -Email,LastName resources/data/*.*

Sort by birth date:

    $ clojure -M:run --sort DateOfBirth resources/data/*.*

Sort by last name descending:

    $ clojure -M:run --sort -LastName resources/data/*.*

### Web Server

To start the http server:

    $ clojure -X:web
    listening on http://localhost:4466

or you can use `bin/web`.
Ctrl-C to stop it.

To specify the port:

    $ clojure -X:web :port 5000
    listening on http://localhost:5000

Example requests:

    $ curl -v -H "Content-type: text/plain" --data-binary @resources/data/enlisted.ssv http://localhost:4466/records
    $ curl http://localhost:4466/records/email
    $ curl http://localhost:4466/records/birthdate
    $ curl http://localhost:4466/records/name

You can also specify the sort fields in the query string using the same format as the CLI:

    $ curl http://localhost:4466/records?sort=-Email,FirstName


## Tests

Run tests

    $ clojure -M:test:runner
