import re

def rewrite_table(match):
    block = match.group(0)
    
    # Strictly target test cases
    if (r'TC\_' in block or r'FT\_' in block or r'DT\_' in block or r'RT\_' in block):
        lines = block.split('\n')
        out_lines = []
        
        header_started = False
        for i, line in enumerate(lines):
            if line.startswith(r'\begin{longtable}'):
                # Smaller widths
                out_lines.append(r'\begin{longtable}{|c|p{4cm}|p{2cm}|p{2.5cm}|p{2.5cm}|p{2.5cm}|p{2.5cm}|}')
            elif r'\textbf{' in line and '&' in line and not header_started:
                header_started = True
                out_lines.append(r'\textbf{\#} & \textbf{Summary} & \textbf{Dependency} & \textbf{Pre-condition} & \textbf{Post-cond.} & \textbf{Execution Steps} & \textbf{Expected Output} \\')
            elif r'\textbf{' in line and '&' in line and header_started:
                pass # skip
            elif '&' in line and '\\\\' in line and not line.strip().startswith(r'\textbf{') and (r'TC\_' in line or r'FT\_' in line or r'DT\_' in line or r'RT\_' in line):
                parts = line.split('&')
                parts = [p.strip() for p in parts]
                last_part = parts[-1].replace('\\\\', '').strip()
                parts[-1] = last_part
                
                old_len = len(parts)
                tc_id = parts[0]
                
                # Context defaults
                dependency = "None"
                pre = "System Active"
                post = "Logged"
                
                if r'DT\_' in tc_id:
                    dependency = "Input Schema"
                    pre = "Validation Context Loaded"
                    post = "State Unchanged"
                elif r'FT\_' in tc_id:
                    dependency = "DB \\newline Session"
                    pre = "App/DB Synced"
                    post = "Data Created"
                elif r'RT\_' in tc_id:
                    dependency = "Error Bounds"
                    pre = "Invalid/Edge Data"
                    post = "Graceful Failure"
                elif r'TC\_AUTH\_' in tc_id:
                    dependency = "User Schema"
                    pre = "No Active Session"
                    post = "Auth Cookie Set"
                elif r'TC\_ORD\_' in tc_id:
                    dependency = "Cart State"
                    pre = "Valid User Session"
                    post = "Order Confirmed"
                
                if old_len == 7:
                    # Already formatted by previous script, we just need to replace the generated columns (indices 2,3,4) with our contexts!
                    # parts = [ID, Summary, Dep, Pre, Post, Exec, Expected]
                    summary = parts[1]
                    exec_steps = parts[5]
                    expected = parts[6]
                else:
                    if old_len == 4: 
                        summary = parts[1]
                        exec_steps = parts[2]
                        expected = parts[3]
                    elif old_len == 3:
                        summary = parts[1]
                        exec_steps = "Execute Test"
                        expected = parts[2]
                    else:
                        summary = parts[1] if old_len > 1 else ""
                        exec_steps = "Execute Test"
                        expected = parts[-1] if old_len > 1 else ""
                
                new_line = f"{tc_id} & {summary} & {dependency} & {pre} & {post} & {exec_steps} & {expected} \\\\"
                out_lines.append(new_line)
            else:
                out_lines.append(line)
        return '\n'.join(out_lines)
    return block

with open('Online_Food_Delivery_System_Test_Report.tex', 'r') as f:
    text = f.read()

new_text = re.sub(r'\\begin\{longtable\}.*?\\end\{longtable\}', rewrite_table, text, flags=re.DOTALL)

with open('Online_Food_Delivery_System_Test_Report.tex', 'w') as f:
    f.write(new_text)
