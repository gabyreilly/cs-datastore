# cs-datastore

The classes submitted here perform the following tasks:

Import command
--------------
Imports from a file path given on the command line. 
Working with the assumption that the data is too large to store in memory, 
and with the restriction to not use SQL or NoSQL,
the input data is sharded and saved into flat files in the datastore folder.

The input file must be of the format:

```
STB|TITLE|PROVIDER|DATE|REV|VIEW_TIME
stb1|the matrix|warner bros|2014-04-01|4.00|1:30
stb1|unbreakable|buena vista|2014-04-03|6.00|2:05
stb2|the hobbit|warner bros|2014-04-02|8.00|2:45
stb3|the matrix|warner bros|2014-04-02|4.00|1:05
```

Subsequent imports of the same logical row (STB, TITLE, and DATE) should overwrite the old row in the shard.

Query command
-------------
The query command supports command line arguments:

-s for selected fields (-s TITLE,REV,DATE), when the fields are returned, the values are separated by commas

-o for row ordering (-o DATE,TITLE), always ascending

-f for filtering (-f DATE=2014-04-01)

Because we still assume that the data is too large to store in memory, the command maps the query over the shards and 
reduces to a single data set.


Tests
-----
Tests are available in the tests package. The supporting classes like MapQuery and QueryParameters are extensively tested,
leaving tests for the commands like Import and Query relatively light.

Considerations
--------------
Since the solution could not use SQL or NoSQL, I used flat files to represent the uploaded 
 data store.  This poses problems for the map/reduce algorithm, because the mapped data must also 
 be stored in flat files since we assume that the data store is too large to fit in memory.
 This would be very impractical for a production system, both in terms of storage space and performance.