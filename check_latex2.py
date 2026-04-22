import re
with open("Online_Food_Delivery_System_Test_Report.tex", "r") as f:
    text = f.read()

lines = text.split('\n')
for i, line in enumerate(lines):
    # Check for unescaped %
    if re.search(r'(?<!\\)%', line):
       if not line.strip().startswith('%'):
           print(f"Line {i+1} has unescaped % inside text: {line}")

    # Check for unescaped #
    if re.search(r'(?<!\\)#', line):
       print(f"Line {i+1} has unescaped #: {line}")

    # Check for unescaped & outside of tabular or longtable
    # Actually just look for all & and manually review
