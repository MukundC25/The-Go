import re

def convert_table(match):
    block = match.group(0)
    
    # Strictly check if this is a test case table
    if (r'TC\_' in block or r'FT\_' in block or r'DT\_' in block or r'RT\_' in block):
        lines = block.split('\n')
        out_lines = []
        
        header_started = False
        for i, line in enumerate(lines):
            if line.startswith(r'\begin{longtable}'):
                out_lines.append(r'\begin{longtable}{|c|p{2.5cm}|p{2cm}|p{2cm}|p{2cm}|p{3.5cm}|p{2.5cm}|}')
            elif r'\textbf{' in line and '&' in line and not header_started:
                header_started = True
                out_lines.append(r'\textbf{\#} & \textbf{Summary} & \textbf{Dependency} & \textbf{Pre-condition} & \textbf{Post-cond.} & \textbf{Execution Steps} & \textbf{Expected Output} \\')
            elif r'\textbf{' in line and '&' in line and header_started:
                pass # ignore repeated headers
            elif '&' in line and '\\\\' in line and not line.strip().startswith(r'\textbf{') and (r'TC\_' in line or r'FT\_' in line or r'DT\_' in line or r'RT\_' in line):
                # Data row
                parts = line.split('&')
                parts = [p.strip() for p in parts]
                last_part = parts[-1].replace('\\\\', '').strip()
                parts[-1] = last_part
                
                old_len = len(parts)
                tc_id = parts[0]
                
                # Mapping logic
                dependency = "None"
                pre = "App running"
                post = "Validated"
                
                if old_len == 4: # ID, Scenario, Input/Steps, Expected
                    summary = parts[1]
                    exec_steps = parts[2]
                    expected = parts[3]
                elif old_len == 3: # ID, Focus/Action, Result
                    summary = parts[1]
                    exec_steps = "Execute standard flow"
                    expected = parts[2]
                else:
                    summary = parts[1] if old_len > 1 else ""
                    exec_steps = "Execute standard flow"
                    expected = parts[-1] if old_len > 1 else ""
                
                new_line = f"{tc_id} & {summary} & {dependency} & {pre} & {post} & {exec_steps} & {expected} \\\\"
                out_lines.append(new_line)
            else:
                out_lines.append(line)
        return '\n'.join(out_lines)
    return block

with open('Online_Food_Delivery_System_Test_Report.tex', 'r') as f:
    text = f.read()

new_text = re.sub(r'\\begin\{longtable\}.*?\\end\{longtable\}', convert_table, text, flags=re.DOTALL)

with open('Online_Food_Delivery_System_Test_Report.tex', 'w') as f:
    f.write(new_text)
