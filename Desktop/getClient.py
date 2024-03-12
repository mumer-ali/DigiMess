import supabase as db

def get_client():
    url = "https://rdgomvjmsabrydklvvee.supabase.co"
    key = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InJkZ29tdmptc2Ficnlka2x2dmVlIiwicm9sZSI6ImFub24iLCJpYXQiOjE3MDQ5NjYxMzksImV4cCI6MjAyMDU0MjEzOX0.pDGt8-NqPHpgZChkFBR0nd_69Jyk_cjiISy_m-OynlM"
    client = db.create_client(url, key)
    return client

def check_login(username, password):
    response = get_client().table("Managers").select("*").execute()
    for i in response:
        for manager in i[1]:
            if manager["username"] == username and manager["password"] == password:
                return manager["first_name"], manager["last_name"], manager["hostel"]
            return False, False, False
        break

