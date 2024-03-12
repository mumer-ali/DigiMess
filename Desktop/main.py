from customtkinter import *
from tkinter import messagebox, StringVar
from tkinter import PhotoImage
from PIL import Image, ImageTk
from getClient import *
from CTkListbox import *
from loginBackend import *
from getHostelites import *
global attendance_list; attendance_list = []
global actual_list; actual_list = []
global student_names
student_names = []
global user
user = ""
global usersname; usersname = ""
global hostel; hostel = ""
def login():
    try:
        global user, usersname, hostel
        username = user_entry.get().lower()
        password = password_entry.get()
        first_name, last_name, hostel = check_login(username=username, password=password)
        if first_name and last_name and hostel:
            user_entry.delete(0, "end")
            password_entry.delete(0, "end")
            user = first_name + " " + last_name
            usersname = username
            Frame.place_forget()
            bigframe.place(x=0,y=0)
            userinfo1.configure(text=user)
            userinfo2.configure(text=usersname)
        else:
            messagebox.showerror("Error", "Login Unsuccessful!")
    except:
            messagebox.showerror("Error", "Check Wifi Connection.")

def clearentry():
                existp.delete(0, "end")
                newp1.delete(0, "end")
                newp2.delete(0, "end")
def logout():
    clearentry()
    cnotFrame.place_forget()
    markframe.place_forget()
    place00(bigframe,cpassframe,Frame,350,250)

def closenot():
    cnotFrame.place_forget()

def place00(x,y,z, w, h):
    clearentry()
    x.place_forget()
    y.place_forget()
    z.place(x=w,y=h)

def markattendance():
    clearentry()
    global attendance_list, student_names
    place00(cpassframe, cnotFrame, markframe, 0, 0)
    student_names = getHostelites(hostel)
    attendance_list = ['P'] * len(student_names)
    for i, (email, name) in enumerate(student_names):
        checkbox_var = BooleanVar()
        checkbox = CTkCheckBox(atten_scrollbar, variable=checkbox_var, hover_color="#3D5A80")
        checkbox.configure(text = name)
        checkbox.grid(row=5*i, column=2, sticky="w", padx=10, pady=5)
        attendance_checkboxes[email] = (checkbox_var, i)

def check_notifications():
    clearentry()
    place00(cpassframe, markframe, cnotFrame, 0, 0)
    if getLists(hostel):
        lb = getLists(hostel)
        for notifications in lb:
            i = 0
            x = ""
            for components in notifications:
                if i == 0:
                    x =components
                else:
                    x = x + " — " + components
                i = i + 1
            notlist.insert(END, x)
    else:
        messagebox.showinfo("INFO", f"NO NOTIFICATION FOR {getDate()}")

def submit_attendance():
    date = getDate()
    for email, (var, index) in attendance_checkboxes.items():
        attendance_list[index] = 'P' if var.get() else 'A'
        x = email, date, attendance_list[index]
        actual_list.append(x)
    list_to_upsert = [{"email": email, "date": date, "state": state} for email, date, state in actual_list]
    get_client().table("Attendance").upsert(list_to_upsert).execute()
    messagebox.showinfo("INFO", f"Attendance Marked FOR {getDate()}")
         
set_appearance_mode("light")

# Create a window
window = CTk(fg_color="#3D5A80")
window.iconbitmap("icon.ico")

screen_width = window.winfo_screenwidth()
screen_height = window.winfo_screenheight()

w = 900
h = 700
x = (screen_width-w)/2
y = (screen_height-h)/2 - 20
window.geometry("%dx%d+%d+%d"%(w,h,x,y))
window.title("DigiMess")
window.resizable(False, False)

#login frame 
Frame = CTkFrame(window, width = 250, height=250, fg_color="#CCCCCC")
Frame.place(x=325,y=215)

#label
manlabel = CTkLabel(Frame, text= "Manager Login", text_color="#001F3F" )
manlabel.configure(font=("Helvetica", 18, "bold"))
manlabel.place(x = 65, y = 25)

#username
user_entry = CTkEntry(Frame, width = 175, height = 30, placeholder_text="Username")
user_entry.place(x = 35, y = 70)

#password
password_entry = CTkEntry(Frame, width = 175, height = 30, placeholder_text="Password", show="*")
password_entry.place(x = 35, y = 125)

#Login button
loginbtn = CTkButton(Frame, text='Login', width = 20, height= 35, corner_radius=10, fg_color="#3D5A80" , command=lambda: login())
loginbtn.place(x= 100 ,y= 180)

bigframe = CTkFrame(window, width = 900, height = 700, corner_radius=0,fg_color="#CCCCCC")
menu_frame = CTkFrame(bigframe, width = 300, height = 700, corner_radius=0, fg_color="#3D5A80")
menu_frame.place(x=0,y=0)

man_pic= Image.open("profile.png") 
man_pic = man_pic.resize((100, 100)) 
photo = ImageTk.PhotoImage(man_pic)

imglabel = CTkLabel(menu_frame, text="")
imglabel.img = photo
imglabel.configure(image=photo, corner_radius=100)
imglabel.place (x = 20, y =100)

userinfo1 = CTkLabel(menu_frame, text = user, text_color="#cccccc")
userinfo1.place(x=120, y = 110)
userinfo1.configure(font=("Helvetica", 15, "bold"))

userinfo2 = CTkLabel(menu_frame, text = usersname, text_color="#cccccc")
userinfo2.place(x=120, y = 140)
userinfo2.configure(font=("Helvetica", 15, "bold"))

# mark attendance
mattenbtn = CTkButton(menu_frame, text='Mark\nAttendance', width = 110, height = 100, corner_radius=10,fg_color="#CCCCCC", text_color= "BLACK", command=lambda: markattendance())
mattenbtn.place(x= 25 ,y= 225)
mattenbtn.configure(font=("Helvetica", 13, "bold"))
# check notifications
cnotbtn = CTkButton(menu_frame, text='Check\nNotifications', width = 110, height = 100, corner_radius=10, fg_color="#CCCCCC",text_color= "BLACK",command=lambda: check_notifications())
cnotbtn.place(x= 160 ,y= 225)
cnotbtn.configure(font=("Helvetica", 13, "bold"))
#change password
cpassbtn = CTkButton(menu_frame, text='Change\nPassword', width = 110, height = 100, corner_radius=10,fg_color="#CCCCCC", text_color= "BLACK",command=lambda: place00(markframe, cnotFrame, cpassframe, 175, 225))
cpassbtn.place(x= 25 ,y= 375)
cpassbtn.configure(font=("Helvetica", 13, "bold"))
#logout
logoutbtn = CTkButton(menu_frame, text='Logout', width = 110, height = 100, corner_radius=10, fg_color="#CCCCCC",text_color= "BLACK",command=lambda: logout())
logoutbtn.place(x= 160 ,y= 375)
logoutbtn.configure(font=("Helvetica", 13, "bold"))
optionframe = CTkFrame(bigframe, height = 700, width=600, corner_radius=0,fg_color="#CCCCCC")
optionframe.place(x=300,y=0)

# check notifications
cnotFrame = CTkFrame(optionframe, height=700, width=600)
notlist = CTkListbox(cnotFrame, height=550 , width=580, text_color = "BLACK")
notlist.place(x=0,y=60)

datelabel = CTkLabel(cnotFrame, text=f"Notifications for {getDate()}",text_color="#3D5A80")
datelabel.configure(font=(("Helvetica", 36, "bold")))
datelabel.place(x = 70, y =10)

closebtn = CTkButton(cnotFrame, text = "CLOSE",width = 100, height = 50, fg_color="#3D5A80" , command=lambda: closenot())
closebtn.configure(font=("Helvetica", 12, "bold"))
closebtn.place(x=260, y= 640)

# Create mark attendance frame
markframe = CTkFrame(optionframe, height=700, width=600, fg_color="#CCCCCC") 

atten_scrollbar = CTkScrollableFrame(markframe, height=570, width=580,fg_color="#CCCCCC", corner_radius=10)
atten_scrollbar.place(x=0,y=50)

attendance_checkboxes = {}

datelabel = CTkLabel(markframe, text=f"Attendance for {getDate()}",text_color="#3D5A80")
datelabel.configure(font=(("Helvetica", 36, "bold")))
datelabel.place(x = 70, y =10)

markbtn = CTkButton(markframe, text = "Mark Attendance", width = 50, height = 50, fg_color="#3D5A80" ,command=lambda: submit_attendance())
markbtn.configure(font=("Helvetica", 12, "bold"))
markbtn.place(x=250, y= 630)

# CHANGE PASSWORD
cpassframe = CTkFrame(optionframe, width = 250, height = 250, fg_color="#3D5A80")

cplabel = CTkLabel(cpassframe, text = "Change Password", text_color="#cccccc")
cplabel.configure(font=("Helvetica", 20, "bold"))
cplabel.place(x=40,y=20)

existp = CTkEntry(cpassframe, placeholder_text="Existing Password",width = 150, height = 30, show ="*")
existp.place(x=50, y= 65)

newp1 = CTkEntry(cpassframe, placeholder_text="New Password",width = 150, height = 30, show ="*")
newp1.place(x=50, y= 110)

newp2 = CTkEntry(cpassframe, placeholder_text="Confirm New Password",width = 150, height = 30, show ="*")
newp2.place(x=50, y= 155)

cpbtn = CTkButton(cpassframe, text = "Change Password", command=lambda: cpassword(usersname, existp, newp1, newp2),text_color="#3D5A80",fg_color="#cccccc")
cpbtn.place(x=55, y= 200)

# looping the main window
window.mainloop()