#pragma once
class Heap
{
public:
	Heap(int capacity);
	Heap(int capacity, bool isLittleTop);
	~Heap();
	virtual void offer(int e);
	virtual int poll();
	virtual int size();
private:
	int* heap;
	int capacity;
	int _size = 0;
	bool isLittleTop;
	void up(int index);
	void down();
};

