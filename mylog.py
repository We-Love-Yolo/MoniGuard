from datetime import datetime


def log(message: str, class_name: str):
    print(f'[+] ({datetime.now()}) - {class_name}: {message}')


def warn(message: str, class_name: str):
    print(f'\033[33m[!] ({datetime.now()}) - {class_name}: {message}\033[0m')


def error(message: str, class_name: str):
    print(f'\033[31m[-] ({datetime.now()}) - {class_name}: {message}\033[0m')