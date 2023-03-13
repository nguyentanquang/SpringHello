    subprocess.check_call(["id", username])
    print(f"User {username} already exists")
except subprocess.CalledProcessError:
    print(f"User {username} does not exist - creating new user")
