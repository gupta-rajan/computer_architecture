    .data
a:
	120
	80
	30
	20
	15
	.text
main:
    addi %x0,80,%x2
    addi %x0,0,%x3
loop:
	load $a, 4, %x3
	load $a, 1, %x4  
	load $a, 4, %x5  
	load $a, 3, %x6  
	load $a, 4, %x7  
	addi %x2, 1, %x2
	blt %x2, %x1, loop
	end