import supabase as db
import re
from datetime import *
from tkinter import messagebox 

def get_client():
    url = "https://rdgomvjmsabrydklvvee.supabase.co"
    key = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InJkZ29tdmptc2Ficnlka2x2dmVlIiwicm9sZSI6ImFub24iLCJpYXQiOjE3MDQ5NjYxMzksImV4cCI6MjAyMDU0MjEzOX0.pDGt8-NqPHpgZChkFBR0nd_69Jyk_cjiISy_m-OynlM"
    client = db.create_client(url, key)
    return client
def getDate():
    date = datetime.now().date()
    date = date.strftime('%d-%m-%Y')
    return date

def getLists(hostel): # Global hostel variable
    hostelites = []
    notifications = []
    response = get_client().table("Hostelites").select("email").eq("hostel", hostel).execute()
    for i in response:
        for user in i[1]:
            hostelites.append(user["email"].split('@')[0])
        break
    response1 = get_client().table("History").select("username,action").eq("date", getDate()).execute() # Replace "23-12-2023" with getDate()
    for i in response1:
        for request in i[1]:
            if request["username"] in hostelites:
                tup = (request["username"], request["action"])
                notifications.append(tup)
        break
    if notifications == []:
        return False
    return notifications

def check_login(username, password):
    response = get_client().table("Managers").select("*").eq("username", username).eq("password", password).execute()
    for i in response:
        for manager in i[1]:
            if manager is not None and manager != "":
                return manager["first_name"], manager["last_name"], manager["hostel"]
            return False, False, False
        break

def validate_password(create):
    if len(create) >= 8:
        # Characters Only
        if re.match("^[a-zA-Z]*$", create):
            return False
        # Numbers Only
        elif re.match("^[0-9]*$", create):
            return False
        # Specials Only
        elif re.match("^[^a-zA-Z0-9]*$", create):
            return False
        else:
            return True
    else:
        return False
    
def cpassword(username, x, y, z): # Global usersname variable
    ep = x.get()
    np1 = y.get()
    np2 = z.get()
    oldPass = ""
    response = get_client().table("Managers").select("password").eq("username", username).execute()
    for i in response:
        for user in i[1]:
            oldPass = user["password"]
            break
        break
    if oldPass == ep:
        if np1 == np2:
            if validate_password(np1):
                get_client().table("Managers").update({"password": np1}).eq("username", username).execute()
                x.delete(0, "end")
                y.delete(0, "end")
                z.delete(0, "end")
            else:
                # password must be >=8 having alphabets, numbers and special characters
                messagebox.showerror("INVALID PASSWORD", "Password must have at least 8 characters and must contain numbers and special characters")
        else:
            # Passwords do not match
            messagebox.showerror("INVALID PASSWORD", "New passwords do not match")
    else:
        # Incorrect Old Paswword
        messagebox.showerror("INVALID PASSWORD", "Incorrect Old Password")

# print(getLists("Attar"))
        
    #     supabaseUrl = "https://rdgomvjmsabrydklvvee.supabase.co",
    # supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InJkZ29tdmptc2Ficnlka2x2dmVlIiwicm9sZSI6ImFub24iLCJpYXQiOjE3MDQ5NjYxMzksImV4cCI6MjAyMDU0MjEzOX0.pDGt8-NqPHpgZChkFBR0nd_69Jyk_cjiISy_m-OynlM",