File filters = new File(basedir, "src/main/content/META-INF/vault/filter.xml")

assert filters.isFile()

String xml = filters.text

assert xml.equals('''<?xml version="1.0" encoding="UTF-8"?>
<workspaceFilter version="1.0">
  <filter root="/content/test">
    <exclude pattern="/content/test(/.*)?/qa(/.*)?"/>
  </filter>
  <filter root="/content/dam/test">
    <exclude pattern="/content/dam/test(/.*)?/renditions(/.*)?"/>
    <include pattern="/content/dam/test(/.*)?/renditions/original"/>
    <exclude pattern="/content/dam/test(/.*)?/qa(/.*)?"/>
  </filter>
</workspaceFilter>
''')
