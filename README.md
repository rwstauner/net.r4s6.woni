# woni

Your data: Was Outside, Now Inside.


## Usage

### CLI

For usage info:

    $ clojure -M:run --help

or you can use `bin/cli ...`.
Run the cli passing in input files and optional sort fields:

    $ clojure -M:run --sort fields,to,sort input.csv...
    record1
    record2

### Web Server

To start the http server:

    $ clojure -X:web
    listening on http://localhost:4466

or you can use `bin/web`.
Ctrl-C to stop it.

Example requests:

    $ curl -v -H "Content-type: text/plain" --data-binary @resources/data/enlisted.ssv http://localhost:4466/records
    $ curl -v http://localhost:4466/records/email


## Tests

Run tests

    $ clojure -M:test:runner
