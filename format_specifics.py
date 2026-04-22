import re

def process_tex():
    with open('Online_Food_Delivery_System_Test_Report.tex', 'r') as f:
        text = f.read()

    # Revert the accidental comment escaping from previous script if it exists
    text = text.replace('\% Chapter', '% Chapter')
    text = text.replace('\%  Chapter', '% Chapter')

    # Dictionary of contexts per test case or keyword
    contexts = {
        'DT_001': ('None', 'Input string format validation loaded', 'System accepts valid format'),
        'DT_002': ('None', 'Input string format validation loaded', 'System rejects invalid format'),
        'DT_003': ('None', 'Phone format validation loaded', 'System accepts 10-digit format'),
        'DT_004': ('None', 'Phone format validation loaded', 'System raises error for <10 digits'),
        'DT_005': ('None', 'Quantity boundary checks loaded', 'Accepts minimum bound value'),
        'DT_006': ('None', 'Quantity boundary checks loaded', 'Accepts maximum bound value'),
        'DT_007': ('None', 'Quantity boundary checks loaded', 'Rejects zero quantity'),
        'DT_008': ('None', 'PIN validation loaded', 'Accepts 6-digit numeric PIN'),
        'DT_009': ('None', 'PIN validation loaded', 'Rejects non-6-digit PIN'),
        
        'FT_001': ('Auth Module', 'User account exists', 'Order fully tracked and completed'),
        'FT_002': ('Admin Module', 'Restaurant account exists', 'Restaurant live on portal'),
        'FT_003': ('Order Module', 'Order pending', 'Payment refunded to source'),
        'FT_004': ('Auth Module', 'Active session exists', 'Database profile updated'),
        'FT_005': ('None', 'Email exists in system', 'Password securely overwritten'),

        'RT_001': ('Payment API', 'Gateways mocked/offline', 'Order mapped to Pending state'),
        'RT_002': ('DB Context', 'DB instance offline', 'Fallback cache served to client'),
        'RT_003': ('Ext API', 'Rest timeout triggered', 'Generic user error displayed'),
        'RT_004': ('Transaction', 'Multi requests active', 'Data locks prevent collision'),
        'RT_005': ('Session Auth', 'Session token expired', 'Immediate redirect to Auth'),
        'RT_006': ('Cart Context', 'Cart quantity = 0', 'Checkout button disabled'),
        'RT_007': ('Payment API', 'Simulator card expired', 'Gateway declines transaction'),
        'RT_008': ('Auth Module', 'Non-admin execution', 'Redirected to 403 Forbidden'),
        'RT_009': ('DB Context', 'Malicious string in input', 'Characters sanitized safely'),
        'RT_010': ('UI Renderer', 'Script tags in payload', 'HTML escaped on screen'),

        'TC_AUTH_001': ('None', 'Valid payload prepared', 'Account created successfully'),
        'TC_AUTH_002': ('TC_AUTH_001', 'Email already in DB', 'Duplicate validation displayed'),
        'TC_AUTH_003': ('None', 'Bad format payload prepared', 'Client validation triggers'),
        'TC_AUTH_004': ('None', 'Weak pass payload prepared', 'Error on strength requirement'),
        'TC_AUTH_005': ('TC_AUTH_001', 'Registered account exists', 'Session cookie granted'),
        'TC_AUTH_006': ('TC_AUTH_001', 'Registered account exists', 'Auth failed error displayed'),
        'TC_AUTH_007': ('None', 'Non-existent account', 'User not found error displayed'),
        'TC_AUTH_008': ('TC_AUTH_001', 'Registered account exists', 'OTP/Link emailed to user'),
        'TC_AUTH_009': ('TC_AUTH_001', 'Account exists and active', 'Account temporarily locked'),
        'TC_AUTH_010': ('TC_AUTH_005', 'Active user session running', 'Cookie correctly invalidated'),

        'TC_ORD_001': ('TC_AUTH_005', 'Auth session, menu loaded', 'Item mapped to Cart state'),
        'TC_ORD_002': ('TC_ORD_001', 'Item exists in user Cart', 'Calculated sum updated'),
        'TC_ORD_003': ('TC_ORD_001', 'Item exists in user Cart', 'Cart becomes completely empty'),
        'TC_ORD_004': ('TC_ORD_001', 'Cart items currently exist', 'Discount calculation applied'),
        'TC_ORD_005': ('TC_ORD_001', 'Cart items currently exist', 'Invalid promo error displayed'),
        'TC_ORD_006': ('TC_ORD_001', 'Cart items currently exist', 'Taxes precisely aggregated'),
        'TC_ORD_007': ('TC_ORD_006', 'Cart fully populated', 'Order pending COD confirmation'),
        'TC_ORD_008': ('TC_ORD_006', 'Cart fully populated', 'Redirected to Payment Auth'),
        'TC_ORD_009': ('TC_ORD_008', 'Order in Processing state', 'Order successfully dropped out'),
        'TC_ORD_010': ('TC_ORD_008', 'Order in Cooking state', 'Cancellation correctly barred'),
    }

    def rewrite_table(match):
        block = match.group(0)
        if r'TC\_' in block or r'FT\_' in block or r'DT\_' in block or r'RT\_' in block or 'Total E2E Scenarios' in block:
            lines = block.split('\n')
            out_lines = []
            header_started = False
            for line in lines:
                if line.startswith(r'\begin{longtable}'):
                    # TIGHT LAYOUT (13cm width manual + borders) to fit nicely in 16cm A4 page.
                    out_lines.append(r'\begin{longtable}{|c|p{2cm}|p{1.5cm}|p{2cm}|p{2cm}|p{2.5cm}|p{2cm}|}')
                elif r'\textbf{' in line and '&' in line and not header_started:
                    header_started = True
                    out_lines.append(r'\textbf{\#} & \textbf{Summary} & \textbf{Dependency} & \textbf{Pre-condition} & \textbf{Post-cond.} & \textbf{Execution Steps} & \textbf{Expected Output} \\')
                elif r'\textbf{' in line and '&' in line and header_started:
                    pass
                elif '&' in line and '\\\\' in line and not line.strip().startswith(r'\textbf{'):
                    parts = [p.strip() for p in line.split('&')]
                    parts[-1] = parts[-1].replace('\\\\', '').strip()
                    tc_id = parts[0].replace('\\_', '_')
                    raw_id = tc_id.replace('\_', '_')
                    
                    if raw_id in contexts:
                        dep, pre, post = contexts[raw_id]
                    elif 'Total' in raw_id or 'Passed' in raw_id or 'Failed' in raw_id:
                        dep, pre, post = "None", "System active", "Report generated"
                    else:
                        dep, pre, post = "None", "System loaded", "Validated"
                        
                    # Handle varying lengths from previous iterations
                    if len(parts) >= 6:
                        # Grab original summary, steps, expected but use our context!
                        summary = parts[1]
                        exec_steps = parts[5]
                        expected = parts[6]
                    else:
                        # fallback
                        summary = parts[1] if len(parts) > 1 else ""
                        exec_steps = parts[2] if len(parts) > 2 else "Execute flow"
                        expected = parts[3] if len(parts) > 3 else "Pass"
                        
                    escaped_id = tc_id.replace('_', '\\_') # re-escape for latex
                    new_line = f"{escaped_id} & {summary} & {dep} & {pre} & {post} & {exec_steps} & {expected} \\\\"
                    out_lines.append(new_line)
                else:
                    out_lines.append(line)
            return '\n'.join(out_lines)
        return block

    text = re.sub(r'\\begin\{longtable\}(.*?)\\end\{longtable\}', rewrite_table, text, flags=re.DOTALL)
    
    with open('Online_Food_Delivery_System_Test_Report.tex', 'w') as f:
        f.write(text)

process_tex()
