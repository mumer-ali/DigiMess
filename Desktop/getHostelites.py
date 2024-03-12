import supabase as db
import re
from datetime import *

def get_client():
    url = "https://rdgomvjmsabrydklvvee.supabase.co"
    key = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InJkZ29tdmptc2Ficnlka2x2dmVlIiwicm9sZSI6ImFub24iLCJpYXQiOjE3MDQ5NjYxMzksImV4cCI6MjAyMDU0MjEzOX0.pDGt8-NqPHpgZChkFBR0nd_69Jyk_cjiISy_m-OynlM"
    client = db.create_client(url, key)
    return client

def getHostelites(hostel): # Global hostel variable
    hostelites = []
    notifications = []
    response = get_client().table("Hostelites").select("email,first_name,last_name").eq("hostel", hostel).execute()
    for i in response:
        for user in i[1]:
            name = user["first_name"] + " " + user["last_name"]
            tup = (user["email"], name)
            hostelites.append(tup)
        break
    return hostelites