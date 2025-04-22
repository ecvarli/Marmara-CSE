import socket
import threading
import argparse


class Proxy:
    def __init__(self, proxy_port, server_host, server_port):
        self.proxy_port = proxy_port
        self.server_host = server_host
        self.server_port = server_port

    def start_proxy(self):
        # Create a server socket that listens for client connections
        with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as server_socket:
            server_socket.bind(('0.0.0.0', self.proxy_port))
            server_socket.listen(5)
            print(f"Proxy Server is running on port {self.proxy_port}")

            # Accept and handle incoming connections using a thread pool
            while True:
                client_socket, client_address = server_socket.accept()
                print(f"Accepted connection from {client_address}")
                threading.Thread(target=self.handle_client, args=(client_socket,)).start()

    def generate_content(self, size, title):
        http_content = f'<html><head><title>{title}</title></head><body>' 
        while len(http_content) < size:
            http_content += 'a '
        http_content += '</body></html>'
        return http_content[:size]  # trim to the exact size

    def handle_client(self, client_socket):
        try:
            request = client_socket.recv(1024).decode('utf-8')
            print(f"Request: {request}")

            request_line = request.splitlines()[0]
            method, uri, _ = request_line.split(" ")

            #request is valid (post, delete, put, patch, head, options); send HTTP 501 error. (It makes it automatically, since we are only processing GET requests.)
            #if the request is invalid, send "HTTP 400" error.
            valid_method = {"POST", "GET", "DELETE", "PUT", "PATCH", "HEAD", "OPTIONS"} #postman valid methods.
            if method not in valid_method:
                response = "HTTP/1.1 400 Bad Request\r\n\r\n<h1>400 Bad Request</h1>"
                client_socket.sendall(response.encode('utf-8'))
                return
        
            #only process get requests
            if method != "GET":
                response = "HTTP/1.1 501 Not Implemented\r\n\r\n<h1>501 Not Implemented</h1>"
                client_socket.sendall(response.encode('utf-8'))
                return
    

            size = int(uri.lstrip('/'))
            if size > 9999: #size limitation
                response = "HTTP/1.1 414 URI Too Long\r\n\r\n<h1>URI Too Long</h1>"  
                client_socket.sendall(response.encode('utf-8'))
                return
    
            try:
                size = int(uri.lstrip('/'))
                if size < 100 or size > 20000: #size limitation
                    raise ValueError  
            except ValueError:
                response = "HTTP/1.1 400 Bad Request\r\n\r\n<h1>400 Bad Request</h1>"
                client_socket.sendall(response.encode('utf-8'))
                return
            
        
             # Send the response back to the client
            response = self.forward(uri, ) #not sure about the destination!!!

            client_socket.sendall(response.encode('utf-8'))
        except Exception as e:
            print(f"Error handling client: {e}")
        finally: 
            client_socket.close()

    ##connect to the target server. 
    #server_socket = socket.socket(socket.AF_INET,socket.SOCK_STREAM)
    #server_socket.connect((target_port))

    #Function to forward data between client and server.
    def forward(self, uri):
        with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as server_socket:
            server_socket.connect((self.server_host, self.server_port))
            request_line = f"GET {uri} HTTP/1.1\r\n"
            headers = f"Host: {self.server_host}:{self.server_port}\r\n\r\n"
            server_socket.sendall(request_line.encode('utf-8') + headers.encode('utf-8'))

        #forward the http request to the main server.
        response = b""

        while True:
            #try:
            request = server_socket.recv(4096)
            if len(request) == 0:
                break
                response += request
            #except Exception as e:
             #   response = f"HTPP/1.1 {status_code} {message}\r\n"
              #  response += message
               # client_socket.sendall(response.encode('utf-8'))

            print(f"debug - Proxy received response from server:\n{response.decode('utf-8')}")
            return response.decode('utf-8')
        


    def start_proxy(self):
        #set up the proxy listener
        with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as server_socket:
            server_socket.bind(('0.0.0.0', self.proxy_port))
            server_socket.listen(5)
            print(f"Proxy Server is running on port {self.proxy_port}")

            #accept and handle incoming connections using a thread pool
            while True:
                client_socket, client_address = server_socket.accept()
                print(f"Accepted connection from {client_address}")
                threading.Thread(target=self.handle_client, args=(client_socket,)).start()


if __name__ == "__main__":

    parser = argparse.ArgumentParser(description="Python Proxy Server")
    parser.add_argument("ServerHost", type=str, help="Host of the web server to forward requests")
    parser.add_argument("ServerPort", type=int, help="Port of the web server to forward requests")
    
    args = parser.parse_args()

    # Extract arguments
    server_host = args.ServerHost
    server_port = args.ServerPort


    # Start the proxy server
    proxy_server = Proxy(8888, server_host, server_port)
    proxy_server.start_proxy()

    try:
        while True:
            pass  # Keep running
    except KeyboardInterrupt:
        print("\nShutting down servers...")

        