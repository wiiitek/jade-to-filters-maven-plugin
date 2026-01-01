String expectedFiltersLocation = "target/output"
File actual = new File(basedir, expectedFiltersLocation)

assert actual.exists()
assert actual.isFile()

String xml = actual.text

assert xml.equals('''<?xml version="1.0" encoding="UTF-8"?>
<workspaceFilter version="1.0"/>
''')
