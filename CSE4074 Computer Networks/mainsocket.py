import socket
from threading import Thread
import argparse
import threading


#------------------CONTENT---------------------
#to add a letter with given size in the uri.
def generate_content(size, title="response"):
    http_content = f'<html><head><title>{title}</title></head><body>' 
    while len(http_content) < size:
        http_content += 'a '
    http_content += '</body></html>'
    return http_content[:size]  # trim to the exact size



#-------------------URI EXTRACTION--------------
def parse_uri():
    return 0



#--------------------CLIENT--------------------------------
def handle_client(client_socket, client_address):

    print(f"[INFO] Connected to client: {client_address}")

    try:
        #receive the HTTP request from client
        request = client_socket.recv(2048).decode('utf-8')
        print(f"[REQUEST] From {client_address}:\n{request}")


        #print(f"[DEBUG] raw request: \n{request}")

        #parse the request line
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
        
        try:
            size = int(uri.lstrip('/'))
            if size < 100 or size > 20000: #size limitation
                raise ValueError  

        except ValueError:
            response = "HTTP/1.1 400 Bad Request\r\n\r\n<h1>400 Bad Request</h1>"
            client_socket.sendall(response.encode('utf-8'))
            return
        
        #print(f"[DEBUG] length of the URI: {len(request_line)}")
        #if (len(request_line) + size) > max_proxy_length:
         #   response = "HTTP/1.1 414 URI Too Long\r\n\r\n<h1>414 URI Too Long</h1>"
          #  client_socket.sendall(response.encode('utf-8'))
           # return

        sized_title = f"I am {size} bytes long"
        content = generate_content(size, title = sized_title)
        response = (
            f"HTTP/1.1 200 OK\r\n"
            f"Content-Type: text/html\r\n"
            f"Content-Length: {len(content)}\r\n"
            f"\r\n"
            f"{content}"
        )
        print(f"Server is sending response to proxy:\n{response}")
        client_socket.sendall(response.encode('utf-8'))

    except Exception as e:
        print(f"[ERROR] Error handling client {client_address}: {e}")
    finally:
        client_socket.close()
        print(f"[INFO] Connection to {client_address} closed.")


#--------------------------SERVER--------------------------
def start_server(port):
    server_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    
    #set socket options to allow reuse and avoid binding issues
    server_socket.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
    server_socket.bind(('0.0.0.0', port))  # Bind to all interfaces
    server_socket.listen(5)
    print(f"[INFO] Server listening on port {port}...")

    try:
        while True:
            try:
                #accept a new client connection
                client_socket, client_address = server_socket.accept()
                
                #handle the proxy connection in a new thread
                threading.Thread(target=handle_client, args=(client_socket, client_address)).start()
            except Exception as e:
                print(f"[ERROR] Error accepting connection: {e}")
    finally:
        server_socket.close()
        print("[INFO] Server has been stopped.")

if __name__ == "__main__":



    #parse command-line arguments for port number
    parser = argparse.ArgumentParser(description="Multi-threaded HTTP Server")
    parser.add_argument("port", type=int, help="Port number to run the server on")
    args = parser.parse_args()
    
     # Start the web server in a separate thread
    server_thread = Thread(target=start_server, args=(args.port,))
    server_thread.daemon = True  # Ensures it exits when the main program exits
    server_thread.start()
    print(f"Web server started on port {args.port}")



    try:
        while True:
            pass  # Keep running
    except KeyboardInterrupt:
        print("\nShutting down servers...")