import re

java_file = r"d:\WorkSpace\MPJ\BookStoreOnline\ALL_JAVA_CONTENT.txt"
js_file = r"d:\WorkSpace\MPJ\BookStoreOnline\ALL_JS_CONTENT.txt"
html_file = r"d:\WorkSpace\MPJ\BookStoreOnline\ALL_HTML_CONTENT.txt"

# 1. Parse Java endpoints
java_endpoints = set()
with open(java_file, "r", encoding="utf-8") as f:
    content = f.read()
    
    # Simple regex to find mapping annotations
    # Examples: @GetMapping("/dashboard/revenue"), @RequestMapping("/api/admin")
    
    class_mappings = {}
    current_class_mapping = ""
    current_file = ""
    
    lines = content.split('\n')
    for line in lines:
        if "FILE [" in line and "]:" in line:
            current_file = line
            current_class_mapping = ""
            continue
            
        # Class level mapping
        class_map_match = re.search(r'@RequestMapping\("([^"]+)"\)', line)
        if class_map_match:
            current_class_mapping = class_map_match.group(1)
            
        # Method level mappings
        method_map_match = re.search(r'@(?:Get|Post|Put|Delete|Patch)Mapping\("([^"]*)"\)', line)
        if method_map_match:
            method_mapping = method_map_match.group(1)
            full_mapping = current_class_mapping + method_mapping
            # Normalize path (remove trailing slash, keep parameter brackets)
            full_mapping = full_mapping.replace('//', '/')
            java_endpoints.add(full_mapping)

print("--- JAVA ENDPOINTS ---")
for ep in sorted(java_endpoints):
    print(ep)

# 2. Parse JS endpoint calls
js_endpoints = set()
with open(js_file, "r", encoding="utf-8") as f:
    content = f.read()
    
    # Matches api.get('/path'), api.post(`/path/${id}`), api.request('/path'
    call_matches = re.finditer(r'api\.(?:get|post|put|delete|request)\s*\(\s*[`\'"]([^`\'"?\n]+)[`\'"?]', content)
    for match in call_matches:
        ep = match.group(1)
        # Add /api if not present, because in JS it appends to baseUrl which usually has /api
        # Wait, the js baseUrl is 'http://localhost:8080/api'
        # So JS endpoints are relative to /api.
        if not ep.startswith('/api'):
            ep = '/api' + (ep if ep.startswith('/') else '/' + ep)
        
        # Replace JS template variables ${...} with Java path variables {...}
        ep = re.sub(r'\$\{([^}]+)\}', r'{\1}', ep)
        js_endpoints.add(ep)

# Also check inline script API calls in HTML
with open(html_file, "r", encoding="utf-8") as f:
    content = f.read()
    call_matches = re.finditer(r'api\.(?:get|post|put|delete|request)\s*\(\s*[`\'"]([^`\'"?\n]+)[`\'"?]', content)
    for match in call_matches:
        ep = match.group(1)
        if not ep.startswith('/api'):
            ep = '/api' + (ep if ep.startswith('/') else '/' + ep)
        ep = re.sub(r'\$\{([^}]+)\}', r'{\1}', ep)
        js_endpoints.add(ep)

print("\n--- JS ENDPOINTS CALLED ---")
for ep in sorted(js_endpoints):
    print(ep)

print("\n--- ANALYSIS: CALLED IN JS BUT NOT FOUND IN JAVA ---")
for js_ep in sorted(js_endpoints):
    # Try to match dynamic paths, like /api/books/{id} matching /api/books/{isbn}
    js_regex = re.sub(r'\{[^}]+\}', '[^/]+', js_ep)
    js_regex = "^" + js_regex + "$"
    
    found = False
    for java_ep in java_endpoints:
        java_regex = re.sub(r'\{[^}]+\}', '[^/]+', java_ep)
        java_regex = "^" + java_regex + "$"
        if re.match(java_regex, js_ep) or re.match(js_regex, java_ep):
            found = True
            break
    
    if not found:
        print(f"Missing in Backend: {js_ep}")

