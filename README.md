## Building the Code

Use the IDE [IntelliJ](https://www.jetbrains.com/idea/download/#section=mac) and download [Java 17](https://www.oracle.com/java/technologies/downloads/#jdk17-mac) to build the code. Add the source files to the Project folder.

```bash
src/Tokenization.java
```

## Running the code

```python
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.HashMap;
import java.util.Map;

// returns "usa"
abbreviate("u.s.a.");

// returns ["i", "am", "so", "sad"]
splitstop("i am so sad :(");

// returns "cross"
step1a("crosses");

// returns "cri"
step1b("cries");
```
