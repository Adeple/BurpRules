# Burp Rule Manager
## What is a "Rule"?
A "Rule" is a combination of a condition and an action that can be applied across your Burp Suite proxy history. Examples of conditions you can set are "If a response contains the ___ header..." or "If a response contains the string ___". Then, each matching request/response will have an action performed on them. For example, "...highlight the request/response in Red" or "...add a custom header called ___ with value ___"
## What kinds of conditions and actions can I set for my Rules?
### Conditions
If a request response
- Does/Doesn't contain a specific string
- Does/Doesn't contain a specific RegEx string
- Does/Doesn't contain a specific header
### Actions
- Highlight
- Add Note
- Drop Request/Response
- Add Header (i.e. "Headername: Headercontent")
- Replace Header (i.e. "Headername: Headercontent")
- Remove Header (i.e. "Headername")