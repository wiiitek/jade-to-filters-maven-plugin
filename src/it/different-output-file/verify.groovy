File filters = new File( basedir, "target/output" )

assert filters.exists()
assert filters.isFile()

String xml = filters.text

assert xml.equals('''<?xml version="1.0" encoding="UTF-8"?>
<workspaceFilter version="1.0"/>
''')
