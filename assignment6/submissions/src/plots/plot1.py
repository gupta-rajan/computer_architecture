import matplotlib.pyplot as plt

# Data
x_axis = ["Without Cache","16B", "128B", "512B", "1kB"]
descending_data = [0.0235, 0.025002256, 0.11878216, 0.10448887, 0.09750088]
evenorodd_data = [0.0232, 0.02238806, 0.021978023, 0.021582734, 0.021201413]
fibonacci_data = [0.0224, 0.022807017, 0.053682037, 0.04797048, 0.046044864]
palindrome_data = [0.0238, 0.030265596, 0.07174231, 0.063885264, 0.0601227]
prime_data = [0.0239, 0.03243848, 0.045383412, 0.04347826, 0.04172662]

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
