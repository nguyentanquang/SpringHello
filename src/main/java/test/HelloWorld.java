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
