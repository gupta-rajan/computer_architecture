	.data
	.text
main:
	addi %x0, 80, %x1
	addi %x0, 0, &x2
loop:
	addi %x3, 1, %x3
	addi %x4, 2, %x4
	addi %x9, 3, %x9
	addi %x2, 1, %x2
	addi %x5, 3, %x5
	addi %x6, 1, %x6
	blt %x2, %x1, loop
	end