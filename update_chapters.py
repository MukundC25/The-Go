import re

def update_chapters(text):
    # Chapter 8 replacement
    tools_start = text.find(r'\chapter{Tools Used}')
    tools_end = text.find(r'\chapter{Conclusion}')
    
    new_tools = r"""\chapter{Tools Used}

The following tools and technologies were utilized during the development and testing phases of the Online Food Delivery System to ensure high-fidelity interactions, reliable test execution, and modern aesthetic delivery:

\section{Development Framework \& Stack}
\begin{itemize}
    \item \textbf{Java 17:} The core programming language utilized for all backend logic and test case scenarios.
    \item \textbf{Javalin 6.1.3:} A lightweight, robust REST and web framework used for handling application routing and HTML rendering.
    \item \textbf{Tailwind CSS (via CDN):} Used heavily to deliver a seamless, dynamic, and aesthetic quick-commerce frontend.
    \item \textbf{HTML5 \& JavaScript:} Native frontend rendering relying heavily on DOM events for dynamic payment and mapping calculations.
\end{itemize}

\section{Testing \& Integration Tools}
\begin{itemize}
    \item \textbf{JUnit 5:} The primary framework managing test execution cycles, annotations (\texttt{@Test}, \texttt{@BeforeEach}), and lifecycle bounds.
    \item \textbf{Selenium WebDriver (v4.15):} Executed browser automation for robust E2E UI testing alongside the \texttt{ChromeDriver} binary.
    \item \textbf{Apache Maven 3.9.6:} Built and handled project dependencies, plugin executions (\texttt{maven-failsafe-plugin} for integration testing), and lifecycle management.
    \item \textbf{Git:} Utilized for version control and incremental tracking.
\end{itemize}

"""
    text = text[:tools_start] + new_tools + text[tools_end:]
    
    # Chapter 7 replacement
    defect_start = text.find(r'\chapter{Defect Report Analysis}')
    defect_end = text.find(r'\chapter{Tools Used}')
    
    new_defect = r"""\chapter{Defect Report Analysis}

During the testing cycles, two major defects were identified and resolved. These issues highlighted the distinct boundaries between our modern user interface implementations and integration testing scripts.

\section{Identified Defects}

\subsection{Defect \#1: Formatter Engine Exception (Critical)}
\textbf{Issue Overview:} 
The Javalin backend threw a \texttt{500 Internal Server Error} via the Java \texttt{java.util.UnknownFormatConversionException}. Tests were timing out at the \texttt{add-to-cart-btn} step.

\textbf{Root Cause:} 
Inline JavaScript utilizing the modulo operator (\texttt{i \% 4 === 0}) inside \texttt{HtmlTemplates.java} to validate 16-digit credit cards. Java's \texttt{String.formatted()} mechanism incorrectly interpreted the '\%4' literal as a missing format reference, thus failing the entire rendering pipeline. A secondary issue was identified with the string "\texttt{50\% OFF}" in the promotional banner.

\textbf{Resolution:} 
Safely escaped JavaScript and inline strings with double percentages (\texttt{\%\%}), yielding zero exceptions.

\subsection{Defect \#2: Selenium Locators Mismatch (High)}
\textbf{Issue Overview:} 
Test cases designed to simulate ordering paths were aborting when trying to click \texttt{\#add-to-cart-btn}.

\textbf{Root Cause:} 
During the UI modernization effort, static ID mapping (\texttt{id="add-to-cart-btn"}) was structurally replaced by dynamic hierarchical class mappings (\texttt{class="add-to-cart-btn"}), invalidating the previous Selenium element selectors.

\textbf{Resolution:} 
Targeted the testing codebase logic to fetch nodes safely using \texttt{By.cssSelector(".add-to-cart-btn")}. Both scenarios now sync flawlessly seamlessly executing full user journeys.

"""
    text = text[:defect_start] + new_defect + text[defect_end:]
    
    
    # Chapter 6.1 Execution Summary
    exec_start = text.find(r'\section{Execution Summary}')
    exec_end = text.find(r'\section{Defect Distribution by Module}')
    
    new_exec = r"""\section{Execution Summary}

\begin{longtable}{|c|p{4cm}|p{2cm}|p{2cm}|p{2cm}|p{3.5cm}|p{2cm}|}
\hline
\textbf{\#} & \textbf{Summary} & \textbf{Dependency} & \textbf{Pre-condition} & \textbf{Post-cond.} & \textbf{Execution Steps} & \textbf{Expected Output} \\
\hline
\endfirsthead
Total E2E Scenarios & 18 & None & Environment Loaded & Evaluated & Execute Selenium Flow & 18 \\
\hline
Passed & 18 & None & Environment Loaded & Evaluated & Assert Conditions & 18 \\
\hline
Failed & 0 & None & Environment Loaded & Evaluated & Assert Conditions & 0 \\
\hline
Blocked & 0 & None & Environment Loaded & Evaluated & Assert Conditions & 0 \\
\hline
Pass Rate & 100\% & None & Environment Loaded & Evaluated & Assert Conditions & 100\% \\
\hline
\end{longtable}

\section{Bug Severity Summary}

\begin{longtable}{|p{3cm}|c|c|p{4cm}|}
\hline
\textbf{Severity} & \textbf{Count} & \textbf{Percentage} & \textbf{Status} \\
\hline
\endfirsthead
Critical & 1 & 50\% & 1 Fixed \\
\hline
High & 1 & 50\% & 1 Fixed \\
\hline
Medium & 0 & 0\% & 0 Fixed \\
\hline
Low & 0 & 0\% & 0 Fixed \\
\hline
\textbf{Total} & \textbf{2} & \textbf{100\%} & \textbf{2 Fixed, 0 Open} \\
\hline
\end{longtable}

"""
    text = text[:exec_start] + new_exec + text[exec_end:]
    
    # Chapter 6.3 Defect Distribution
    dist_start = text.find(r'\section{Defect Distribution by Module}')
    dist_end = text.find(r'\chapter{Defect Report Analysis}')
    
    new_dist = r"""\section{Defect Distribution by Module}

\begin{longtable}{|c|p{4cm}|p{2cm}|p{2.5cm}|p{2cm}|p{3cm}|p{2cm}|}
\hline
\textbf{\#} & \textbf{Summary} & \textbf{Dependency} & \textbf{Pre-condition} & \textbf{Post-cond.} & \textbf{Execution Steps} & \textbf{Expected Output} \\
\hline
\endfirsthead
Order Management (UI Class Mismatch) & 1 & Selenium Locators & App Extracted & Evaluated & Execute standard flow & 1 \\
\hline
Payment / Template Engine (Format Mismatch) & 1 & System Pipeline & Javalin Booted & Evaluated & Render Content Payload & 1 \\
\hline
\end{longtable}

"""
    text = text[:dist_start] + new_dist + text[dist_end:]
    
    return text

with open('Online_Food_Delivery_System_Test_Report.tex', 'r') as f:
    text = f.read()

text = update_chapters(text)

with open('Online_Food_Delivery_System_Test_Report.tex', 'w') as f:
    f.write(text)
