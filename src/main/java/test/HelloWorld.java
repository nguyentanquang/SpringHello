import json

def get_item_value(json_file, item_path):
    with open(json_file) as f:
        data = json.load(f)
        item_path_list = item_path.split(".")
        for item in item_path_list:
            data = data.get(item, {})
        return data

print(get_item_value("file.json", "a.b.c")) # Output: value1
print(get_item_value("file.json", "e.b.d")) # Output: v


#!/bin/bash
#!/bin/bash
import json

def get_json_value(file_path, field_name):
    with open(file_path) as f:
        data = json.load(f)
        return data.get(field_name)
function parse_json {
  local json_file=$1
  local field_name=$2
  local value=$(grep -oE "\"$field_name\":\"[^\"]+\"" $json_file | awk -F":" '{print $2}' | tr -d '"')
  echo "Value of $field_name is: $value"
}
# Hàm để lấy giá trị item cấp sâu trong JSON
get_json_value() {
  local json=$1
  local item=$2
  local IFS='.'
  local -a keys=($item)
  local value=''
  
  for key in "${keys[@]}"; do
    if [[ $json =~ \"$key\":\"(.*?)\", ]]; then
      value=${BASH_REMATCH[1]}
      json=${json#*\"$key\":\"$value\",}
    elif [[ $json =~ \"$key\":(.*?), ]]; then
      value=${BASH_REMATCH[1]}
      json=${json#*\"$key\":$value,}
    fi
  done
  
  echo "$value"
}

# Sử dụng hàm
json='{"a": {"b": {"c": "value"}}}'
item="a.b.c"
value=$(get_json_value "$json" "$item")

echo "Giá trị của item 

#!/bin/bash

# Function to find a value of an item in the config file
function find() {
  local item=$1
  local value=$(grep $item config | cut -d ':' -f2)
  echo $value
}

# Create the config file with name-value pairs
echo "item1:value1" > config
echo "item2:value2" >> config
echo "item3:value3" >> config

# Call the find function and pass in the item name as an argument
result=$(find "item2")
echo "Value of item2: $result"


function parse_json {
  local json_file=$1
  local field_name=$2
  local value=$(grep -oE "\"$field_name\":\"[^\"]+\"" $json_file | awk -F":" '{print $2}' | tr -d '"')
  echo "Value of $field_name is: $value"
}

parse_json "file.json" "field_name"

if [ "$(lsb_release -rs | cut -d. -f1)" == "446" ]; then
  yum install -y postgreffsql93
  yum remove -y postgresfffql-libs
fi
Lệnh lsb_release -rs sẽ trả về phiên bản hệ điều hành của bạn và cut -d. -f1 sẽ cắt chuỗi để chỉ giữ lại phần số nguyên đầu tiên của phiên bản.

if yum list installed | grep 'oracle-7' > /dev/null; then
  echo "oracle-7 package is installed"
else
  echo "oracle-7 package is not installed"
fi
