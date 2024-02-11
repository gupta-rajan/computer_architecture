import matplotlib.pyplot as plt

# Data
x_axis = ["Without Cache","16B", "128B", "512B", "1kB"]
descending_data = [0.0235, 0.0800578, 0.10386202, 0.100580975, 0.09750088]
evenorodd_data = [0.0232, 0.021428572, 0.021352313, 0.021276595, 0.021201413]
fibonacci_data = [0.0224, 0.048811015, 0.047852762, 0.04693141, 0.046044864]
palindrome_data = [0.0238, 0.060344826, 0.060270604, 0.06019656, 0.0601227]
prime_data = [0.0239, 0.041907515, 0.041847043, 0.041786745, 0.04172662]

# Create a figure and a set of subplots
fig, ax = plt.subplots()

# Plotting the data
ax.plot(x_axis, descending_data, marker='o', label='descending.asm')
ax.plot(x_axis, evenorodd_data, marker='o', label='evenorodd.asm')
ax.plot(x_axis, fibonacci_data, marker='o', label='fibonacci.asm')
ax.plot(x_axis, palindrome_data, marker='o', label='palindrome.asm')
ax.plot(x_axis, prime_data, marker='o', label='prime.asm')

# Adding title and labels
plt.xlabel('Cache Size')
plt.ylabel('IPC')

# Adding a legend
ax.legend()

# Adding grid
plt.grid(True)

# Show plot
plt.show()
