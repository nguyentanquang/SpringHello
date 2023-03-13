from string import Template

# read the template file
with open("template.txt", "r") as f:
    template_string = f.read()

# create a Template object
template = Template(template_string)

# define the values to substitute
values = {"name": "John", "city": "New York"}

# substitute the values in the template
result = template.substitute(values)

# create a new file and write the result to it
with open("output.txt", "w") as f:
    f.write(result)
