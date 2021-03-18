#include "Heap.h"

Heap::Heap(int capacity) :Heap(capacity, true)
{
}

Heap::Heap(int capacity, bool isLittleTop)
{
	this->capacity =
		capacity;
	this->heap = new int[capacity];
	this->isLittleTop = isLittleTop;
}

Heap::~Heap() {
	delete this->heap;
}

static int inline left(int index) {
	return index * 2 + 1;
}

static int inline right(int index) {
	return index * 2 + 2;
}

static int inline parent(int index) {
	return (index - 1) / 2;
}

void Heap::up(int index) {
	int t1 = this->heap[index];
	int t2 = this->heap[parent(index)];
	while (index != 0 && (t1 < t2 ^ !this->isLittleTop))
	{
		int t = heap[index];
		heap[index] = heap[parent(index)];
		heap[parent(index)] = t;

		index = parent(index);
		int t1 = this->heap[index];
		int t2 = this->heap[parent(index)];
	}
}

void Heap::down() {
	int index = 0;
	int l = left(0);
	int r = right(0);
	while ((l = left(index)) < _size && (r = right(index))) {
		if (r == _size || (heap[l] < heap[r] ^ !isLittleTop))
		{
			int t = heap[index];
			heap[index] = heap[l];
			heap[l] = t;
			index = l;
		}
		else {
			int t = heap[index];
			heap[index] = heap[r];
			heap[r] = t;
			index = r;
		}

	}
}

void Heap::offer(int e)
{
	heap[_size] = e;
	up(_size++);
}

int Heap::poll() {
	int result = heap[0];
	int t = result;
	heap[0] = heap[--_size];
	down();
	return result;
}

int Heap::size() {
	return this->_size;
}