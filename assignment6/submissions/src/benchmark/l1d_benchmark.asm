.data
a:
	38
	48
	58
	68
	78
	.text
main:
	addi %x0, 100, %x1
	addi %x0, 0, &x2
	load %x0, 1, %x10
	load %x0, 2, %x11
	load %x0, 3, %x12
	load %x0, 4, %x13
loop:
	addi %x2, 1, %x2
	load %x0, $a, %x3
	load %x12, $a, %x16
	load %x10, $a, %x14
	load %x11, $a, %x5
	load %x13, $a, %x7
	blt %x2, %x1, loop
	end