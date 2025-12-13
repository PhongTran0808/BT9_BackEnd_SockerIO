#!/usr/bin/env python3
import http.server
import socketserver
import webbrowser
import os

PORT = 8000

class Handler(http.server.SimpleHTTPRequestHandler):
    def __init__(self, *args, **kwargs):
        super().__init__(*args, directory=os.getcwd(), **kwargs)

print("🌐 Starting HTTP server for testing...")
print(f"📍 URL: http://localhost:{PORT}/test-login.html")
print("🔌 Socket.IO server should be running on port 9092")
print("⏹️  Press Ctrl+C to stop")

with socketserver.TCPServer(("", PORT), Handler) as httpd:
    try:
        # Auto open browser
        webbrowser.open(f'http://localhost:{PORT}/test-login.html')
        httpd.serve_forever()
    except KeyboardInterrupt:
        print("\n👋 HTTP server stopped")