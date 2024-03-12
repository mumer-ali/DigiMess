from customtkinter import *
from LoginBackend import *

global usersname, hostel
usersname = "mgr.attar"
hostel = "Attar"

def login():
    global hostel
    username = user_entry.get()
    password =password_entry.get()
    first_name, last_name, hostel = check_login(username=username, password=password)
    if first_name and last_name and hostel:
        print(first_name, last_name)
    else: 
        print("bhago")
    


set_appearance_mode("light")

# Create a window
window = CTk()
screen_width = window.winfo_screenwidth()
screen_height = window.winfo_screenheight()

w = 900
h = 700
x = (screen_width-w)/2
y = (screen_height-h)/2
window.geometry("%dx%d+%d+%d"%(w,h,x,y-20))
window.title("DigiMess") 
window.resizable(False, False)

#login frame 
Frame = CTkFrame(window, width = 200, height=200)
Frame.place(x=350,y=250)

#label
manlabel = CTkLabel(Frame, text= "Manager Login")
manlabel.place(x = 55, y = 20)

#username
user_entry = CTkEntry(Frame, placeholder_text="Username")
user_entry.place(x = 30, y = 60)

#password
password_entry = CTkEntry(Frame, placeholder_text="Password", show="*")
password_entry.place(x = 30, y = 105)

#Login button
loginbtn = CTkButton(Frame, text='Login', width = 15, corner_radius=10, command=lambda: login())
loginbtn.place(x= 75 ,y= 150)
window.mainloop()