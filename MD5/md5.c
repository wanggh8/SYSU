#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <stdint.h>

const uint32_t s[] = {7, 12, 17, 22, 7, 12, 17, 22, 7, 12, 17, 22, 7, 12, 17, 22,
                      5, 9, 14, 20, 5, 9, 14, 20, 5, 9, 14, 20, 5, 9, 14, 20,
                      4, 11, 16, 23, 4, 11, 16, 23, 4, 11, 16, 23, 4, 11, 16, 23,
                      6, 10, 15, 21, 6, 10, 15, 21, 6, 10, 15, 21, 6, 10, 15, 21};

const uint32_t T[64] = {
    0xd76aa478, 0xe8c7b756, 0x242070db, 0xc1bdceee,
    0xf57c0faf, 0x4787c62a, 0xa8304613, 0xfd469501,
    0x698098d8, 0x8b44f7af, 0xffff5bb1, 0x895cd7be,
    0x6b901122, 0xfd987193, 0xa679438e, 0x49b40821,
    0xf61e2562, 0xc040b340, 0x265e5a51, 0xe9b6c7aa,
    0xd62f105d, 0x02441453, 0xd8a1e681, 0xe7d3fbc8,
    0x21e1cde6, 0xc33707d6, 0xf4d50d87, 0x455a14ed,
    0xa9e3e905, 0xfcefa3f8, 0x676f02d9, 0x8d2a4c8a,
    0xfffa3942, 0x8771f681, 0x6d9d6122, 0xfde5380c,
    0xa4beea44, 0x4bdecfa9, 0xf6bb4b60, 0xbebfbc70,
    0x289b7ec6, 0xeaa127fa, 0xd4ef3085, 0x04881d05,
    0xd9d4d039, 0xe6db99e5, 0x1fa27cf8, 0xc4ac5665,
    0xf4292244, 0x432aff97, 0xab9423a7, 0xfc93a039,
    0x655b59c3, 0x8f0ccc92, 0xffeff47d, 0x85845dd1,
    0x6fa87e4f, 0xfe2ce6e0, 0xa3014314, 0x4e0811a1,
    0xf7537e82, 0xbd3af235, 0x2ad7d2bb, 0xeb86d391};

void toByte(uint32_t val, uint8_t *bytes)
{
    bytes[0] = (uint8_t)val;
    bytes[1] = (uint8_t)(val >> 8);
    bytes[2] = (uint8_t)(val >> 16);
    bytes[3] = (uint8_t)(val >> 24);
}

void md5(const uint8_t *initial_msg, size_t initial_len, uint8_t *digest)
{

    uint8_t *msg = NULL;
    size_t new_len, offset;
    uint32_t X[16];
    uint32_t i, k, g, temp; // 总结变量

    uint32_t a, b, c, d; // 寄存器
    // 储存初值
    uint32_t res0 = 0x67452301;
    uint32_t res1 = 0xefcdab89;
    uint32_t res2 = 0x98badcfe;
    uint32_t res3 = 0x10325476;

    // 获取长度，并申请新的字符串，单位是字节数
    for (new_len = initial_len + 1; new_len % (512 / 8) != 448 / 8; new_len++)
        ;
    msg = (uint8_t *)malloc(new_len + 8);
    memcpy(msg, initial_msg, initial_len);

    // 添加一个1和足够的0
    msg[initial_len] = 0x80;
    for (offset = initial_len + 1; offset < new_len; offset++)
    {
        msg[offset] = 0;
    }

    // k的后64位添加到消息尾部
    toByte(initial_len * 8, msg + new_len);
    toByte(initial_len >> (32 - 2), msg + new_len + 4);

    // 对512比特字符分段处理
    for (int j = 0; j < new_len; j += 64)
    {
        // 从内存中读入32位字符串
        for (i = 0; i < 16; i++)
        {
            uint8_t *bytes = msg + j + i * 4;
            X[i] = (uint32_t)bytes[0] | ((uint32_t)bytes[1] << 8) | ((uint32_t)bytes[2] << 16) | ((uint32_t)bytes[3] << 24);
        }

        // 寄存器初始化
        a = res0;
        b = res1;
        c = res2;
        d = res3;

        // 4轮循环:
        for (i = 0; i < 64; i++)
        {

            if (i < 16)
            {
                g = (b & c) | ((~b) & d);
                k = i;
            }
            else if (i < 32)
            {
                g = (d & b) | ((~d) & c);
                k = (5 * i + 1) % 16;
            }
            else if (i < 48)
            {
                g = b ^ c ^ d;
                k = (3 * i + 5) % 16;
            }
            else
            {
                g = c ^ (b | (~d));
                k = (7 * i) % 16;
            }
            // 迭代
            temp = d;
            d = c;
            c = b;
            b = b + (((a + g + X[k] + T[i]) << s[i]) | ((a + g + X[k] + T[i]) >> (32 - s[i])));
            a = temp;
        }

        // 存储结果
        res0 += a;
        res1 += b;
        res2 += c;
        res3 += d;
    }

    // 释放内存
    free(msg);

    // 使用小端储存结果
    toByte(res0, digest);
    toByte(res1, digest + 4);
    toByte(res2, digest + 8);
    toByte(res3, digest + 12);
}

int main()
{
    char msg[100000];
    uint8_t result[16];

    scanf("%[^\n]", msg);
    md5((uint8_t *)msg, strlen(msg), result);

    for (int i = 0; i < 16; i++)
    {
        printf("%2.2x", result[i]);
    }
    puts("");

    system("pause");
    return 0;
}