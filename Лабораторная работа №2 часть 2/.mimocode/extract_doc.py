from pathlib import Path
import re

p = Path(r'C:\Users\Windows 11 Pro\Desktop\Web-development(2nd half)\Лабораторная работа №2 часть 2\Лаб2_Angular.doc')
data = p.read_bytes()
text = data.decode('utf-16-le', errors='ignore')
parts = re.findall(r'[^\x00-\x08\x0b\x0c\x0e-\x1f]{15,}', text)
for part in parts:
    letters = sum(c.isalpha() for c in part)
    if letters >= 10:
        print('---')
        print(part)
