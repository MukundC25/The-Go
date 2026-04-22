import re

with open('Online_Food_Delivery_System_Test_Report.tex', 'r') as f:
    text = f.read()

tables = re.finditer(r'\\begin\{longtable\}(.*?)\\end\{longtable\}', text, re.DOTALL)
for i, t in enumerate(tables):
    block = t.group(0)
    # Check if this table has our headers
    if "Summary & Dependency" in block or "Dependency & Pre-condition" in block:
        print(f"Table {i} has Test Cases format. First line: {block.split(chr(10))[2][:80]}")

