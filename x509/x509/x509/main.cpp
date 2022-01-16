#include <iostream>
#include <string>
#include <cstring>
#include <fstream>
#include <direct.h>
#include <time.h>
#include <stdio.h>
#include <windows.h>
//#include <openssl/evp_locl>
#include <openssl/ossl_typ.h>
#include <openssl/evp.h>
#include <openssl/pem.h>
#include <openssl/x509.h>
#include <openssl/x509.h>
#include <openssl/rsa.h>
#include <openssl/err.h>
#include <openssl/asn1.h>
#include <openssl/engine.h>
#include <openssl/evp.h>

#include <openssl/x509v3.h>

using namespace std;
int mypint(const char **s, int n, int min, int max, int *e)
{
	int retval = 0;
	while (n)
	{
		if (**s < '0' || **s > '9')
		{
			*e = 1;
			return 0;
		}
		retval *= 10;
		retval += **s - '0';
		--n;
		++(*s);
	}
	if (retval < min || retval > max)
		*e = 1;
	return retval;
}

time_t ASN1_TIME_get(ASN1_TIME *a, int *err)
{
	char days[2][12] = {
		{31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31},
		{31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31}};
	int dummy;
	const char *s;
	int generalized;
	struct tm t;
	int i, year, isleap, offset;
	time_t retval;

	if (err == NULL)
		err = &dummy;
	if (a->type == V_ASN1_GENERALIZEDTIME)
	{
		generalized = 1;
	}
	else if (a->type == V_ASN1_UTCTIME)
	{
		generalized = 0;
	}
	else
	{
		*err = 1;
		return 0;
	}
	s = (const char *)a->data;
	if (s == NULL || s[a->length] != '\0')
	{
		*err = 1;
		return 0;
	}
	*err = 0;
	if (generalized)
	{
		t.tm_year = mypint(&s, 4, 0, 9999, err) - 1900;
	}
	else
	{
		t.tm_year = mypint(&s, 2, 0, 99, err);
		if (t.tm_year < 50)
			t.tm_year += 100;
	}
	t.tm_mon = mypint(&s, 2, 1, 12, err) - 1;
	t.tm_mday = mypint(&s, 2, 1, 31, err);
	t.tm_hour = mypint(&s, 2, 0, 23, err);
	t.tm_min = mypint(&s, 2, 0, 59, err);

	if (*s >= '0' && *s <= '9')
	{
		t.tm_sec = mypint(&s, 2, 0, 59, err);
	}
	else
	{
		t.tm_sec = 0;
	}
	if (*err)
		return 0;
	if (generalized)
	{
		while (*s == '.' || *s == ',' || (*s >= '0' && *s <= '9'))
			++s;
		if (*s == 0)
		{
			t.tm_isdst = -1;
			retval = mktime(&t);
			if (retval == (time_t)-1)
			{
				*err = 2;
				retval = 0;
			}
			return retval;
		}
	}
	if (*s == 'Z')
	{
		offset = 0;
		++s;
	}
	else if (*s == '-' || *s == '+')
	{
		i = (*s++ == '-');
		offset = mypint(&s, 2, 0, 12, err);
		offset *= 60;
		offset += mypint(&s, 2, 0, 59, err);
		if (*err)
			return 0;
		if (i)
			offset = -offset;
	}
	else
	{
		*err = 1;
		return 0;
	}
	if (*s)
	{
		*err = 1;
		return 0;
	}
	retval = t.tm_sec;
	retval += (t.tm_min - offset) * 60;
	retval += t.tm_hour * 3600;
	retval += (t.tm_mday - 1) * 86400;
	year = t.tm_year + 1900;
	if (sizeof(time_t) == 4)
	{
		if (year < 1900 || year > 2040)
			*err = 2;
	}
	isleap = ((year % 4 == 0) && (year % 100 != 0)) || (year % 400 == 0);
	for (i = t.tm_mon - 1; i >= 0; --i)
		retval += days[isleap][i] * 86400;
	retval += (year - 1970) * 31536000;
	if (year < 1970)
	{
		retval -= ((1970 - year + 2) / 4) * 86400;
		if (sizeof(time_t) > 4)
		{
			for (i = 1900; i >= year; i -= 100)
			{
				if (i % 400 == 0)
					continue;
				retval += 86400;
			}
		}
		if (retval >= 0)
			*err = 2;
	}
	else
	{
		retval += ((year - 1970 + 1) / 4) * 86400;
		if (sizeof(time_t) > 4)
		{
			for (i = 2100; i < year; i += 100)
			{
				if (i % 400 == 0)
					continue;
				retval -= 86400;
			}
		}
		if (retval < 0)
			*err = 2;
	}
	if (*err)
		retval = 0;
	return retval;
}
void PrintHex(unsigned char *str, unsigned int len)
{
	for (int i = 0; i < len; i++)
	{
		printf("0x%02x ", *(str + i));
	}
	printf("\n");
}
int main()
{
	unsigned char buf[50000], *cerData;
	int len = 0;

	FILE *fp;
	fopen_s(&fp, "qq.cer", "rb");
	len = fread(buf, 1, 50000, fp);
	cerData = buf;
	fclose(fp);

	X509 *m_pX509 = d2i_X509(NULL, (unsigned char const **)&cerData, len);
	if (m_pX509 == NULL)
	{
		cout << "证书为空" << endl;
		return 0;
	}

	//版本号
	int ver = X509_get_version(m_pX509);
	switch (ver)
	{
	case 0: //V1
		cout << "版本号为：V1" << endl;
		break;
	case 1: //V2
		cout << "版本号为：V2" << endl;
		break;
	case 2: //V3
		cout << "版本号为：V3" << endl;
	default:
		//Error!
		break;
	}

	//序列号
	BIGNUM *bignum = NULL;
	auto sn = X509_get_serialNumber(m_pX509);
	bignum = ASN1_INTEGER_to_BN(sn, NULL);
	char *sn1 = BN_bn2hex(bignum);
	BN_free(bignum);
	cout << "序列号为：" << sn1 << endl;
	//printf("%s", sn1);
	OPENSSL_free(sn1);

	//签名算法和公钥
	string pulType;
	auto pk = X509_get_pubkey(m_pX509);
	unsigned char PublicKey[512];
	unsigned char *PKey = PublicKey;
	int PublicKeyLen = 0;

	if (EVP_PKEY_RSA == pk->type)
	{
		pulType = "RSA";
		RSA *rsa = EVP_PKEY_get1_RSA(pk);
		PublicKeyLen = i2d_RSAPublicKey(rsa, &PKey);
	}
	else if (EVP_PKEY_EC == pk->type)
	{
		pulType = "EC";
		ec_key_st *ec = EVP_PKEY_get1_EC_KEY(pk);
		PublicKeyLen = i2d_EC_PUBKEY(ec, &PKey);
	}
	else if (EVP_PKEY_DSA == pk->type)
	{
		pulType = "DSA";
		dsa_st *dsa = EVP_PKEY_get1_DSA(pk);
		PublicKeyLen = i2d_DSA_PUBKEY(dsa, &PKey);
	}
	else if (EVP_PKEY_DH == pk->type)
	{
		pulType = "DH";
		dh_st *dh = EVP_PKEY_get1_DH(pk);
	}
	else
	{
		pulType = "UNKNOWN";
	}

	cout << "签名算法：" << pulType << endl
		 << "公钥:" << endl;
	PrintHex(PublicKey, PublicKeyLen);

	//签名哈希算法
	char oid[128] = {0};
	ASN1_OBJECT *salg = m_pX509->sig_alg->algorithm;
	OBJ_obj2txt(oid, 128, salg, 1);
	cout << "签名哈希算法：" << oid << endl;

	//颁发者
	int nNameLen = 512;
	CHAR csCommonName[512] = {0};
	X509_NAME *pCommonName = X509_get_issuer_name(m_pX509);
	nNameLen = X509_NAME_get_text_by_NID(pCommonName, NID_commonName, csCommonName, nNameLen);
	cout << "颁发者：" << csCommonName << endl;

	//使用者
	int iLen = 0;
	int iSubNameLen = 0;
	CHAR csSubName[1024] = {0};
	CHAR csBuf[256] = {0};

	X509_NAME *pSubName = X509_get_subject_name(m_pX509);

	ZeroMemory(csBuf, 256);
	iLen = X509_NAME_get_text_by_NID(pSubName, NID_countryName, csBuf, 256);
	if (iLen > 0)
	{
		strcat_s(csSubName, 1024, "C=");
		strcat_s(csSubName, 1024, csBuf);
		strcat_s(csSubName, 1024, ", ");
	}
	ZeroMemory(csBuf, 256);
	iLen = X509_NAME_get_text_by_NID(pSubName, NID_organizationName, csBuf, 256);
	if (iLen > 0)
	{
		strcat_s(csSubName, 1024, "O=");
		strcat_s(csSubName, 1024, csBuf);
		strcat_s(csSubName, 1024, ", ");
	}
	ZeroMemory(csBuf, 256);
	iLen = X509_NAME_get_text_by_NID(pSubName, NID_organizationalUnitName, csBuf, 256);
	if (iLen > 0)
	{
		strcat_s(csSubName, 1024, "OU=");
		strcat_s(csSubName, 1024, csBuf);
		strcat_s(csSubName, 1024, ", ");
	}
	ZeroMemory(csBuf, 256);
	iLen = X509_NAME_get_text_by_NID(pSubName, NID_commonName, csBuf, 256);
	if (iLen > 0)
	{
		strcat_s(csSubName, 1024, "CN=");
		strcat_s(csSubName, 1024, csBuf);
	}
	cout << "使用者：" << csSubName << endl;

	//有效期
	int err = 0;
	ASN1_TIME *start = NULL;
	ASN1_TIME *end = NULL;
	time_t ttStart = {0};
	time_t ttEnd = {0};
	LONGLONG nLLStart = 0;
	LONGLONG nLLEnd = 0;
	FILETIME ftStart = {0};
	FILETIME ftEnd = {0};
	LPSYSTEMTIME ptmStart = new SYSTEMTIME, ptmEnd = new SYSTEMTIME;
	start = X509_get_notBefore(m_pX509);
	end = X509_get_notAfter(m_pX509);

	ttStart = ASN1_TIME_get(start, &err);
	ttEnd = ASN1_TIME_get(end, &err);
	nLLStart = Int32x32To64(ttStart, 10000000) + 116444736000000000;
	nLLEnd = Int32x32To64(ttEnd, 10000000) + 116444736000000000;

	ftStart.dwLowDateTime = (DWORD)nLLStart;
	ftStart.dwHighDateTime = (DWORD)(nLLStart >> 32);
	ftEnd.dwLowDateTime = (DWORD)nLLEnd;
	ftEnd.dwHighDateTime = (DWORD)(nLLEnd >> 32);

	FileTimeToSystemTime(&ftStart, ptmStart);
	FileTimeToSystemTime(&ftEnd, ptmEnd);
	char s[100];
	char e[100];
	sprintf_s(s, "%d-%d-%d %d:%d:%d", ptmStart->wYear, ptmStart->wMonth, ptmStart->wDay, ptmStart->wHour, ptmStart->wMinute, ptmStart->wSecond);
	sprintf_s(e, "%d-%d-%d %d:%d:%d", ptmEnd->wYear, ptmEnd->wMonth, ptmEnd->wDay, ptmEnd->wHour, ptmEnd->wMinute, ptmEnd->wSecond);
	cout << "有效期从" << s << endl
		 << "到：" << e << endl;

	return 0;
}
