#include "Heap.h"
#include <stdio.h>
int main() {
	Heap* heap = new Heap(100);
	heap->offer(1);
	heap->offer(1);
	heap->offer(3);
	heap->offer(2);
	heap->offer(5);
	heap->offer(4);
	heap->offer(67);
	heap->offer(7);
	while (heap->size())
	{
		printf("%d\n", heap->poll());
	}
}