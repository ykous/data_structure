from typing import List


def lowbit(n) -> int:
    return n & -n


class BinaryIndexedTree:

    def __init__(self, nums: List[int]):
        self.__private_nums = [0 for _ in range(len(nums))]
        self.__private_prefix = [0 for _ in range(len(nums) + 1)]
        for i in range(len(nums)):
            self.update(i,nums[i])

    def query(self, n: int) -> int:
        """
        查询指定位置的前缀和，注意是右开区间
        :param n:  表示查询 [0,n) 区间的和
        """
        result = 0
        while n > 0:
            result += self.__private_prefix[n]
            n -= lowbit(n)
        return result

    def update(self, index: int, new_value):
        """
        更新数组中指定索引的值为新值
        """
        delta = new_value - self.__private_nums[index]
        self.__private_nums[index] = new_value
        index += 1
        length = len(self.__private_prefix)
        while index < length:
            self.__private_prefix[index] += delta
            index += lowbit(index)


if __name__ == '__main__':
    bit = BinaryIndexedTree([1, 2, 3, 4, 5])
    print(bit.query(1))
    print(bit.query(3))
    print(bit.query(5))
    bit.update(2, -1)
    print(bit.query(1))
    print(bit.query(3))
    print(bit.query(5))
    bit.update(4, 10)
    print(bit.query(1))
    print(bit.query(3))
    print(bit.query(5))
