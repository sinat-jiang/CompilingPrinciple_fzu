#include <iostream>
#include <algorithm>
#include<conio.h>
using namespace std;

int address=100; 		//每条分析语句的地址 
int LID=0;       		//表示过程执行到相应位置的地址符号 
int tID=0;		 		//用于替换表达式的标识符 
int ip=0;
string shuru[666];		//存放从文件读入的字符串
int maxsize;			//设置存放数组的长度 

string biaodashi();

/*****字符串和数字的连接*****/
string l(string a,int b)
{
	string t="";
	do
	{
		t+=b%10+'0';
		b/=10;
	}
	while(b);
	reverse(t.begin(),t.end());
	return a+t;
}

/*****获取表达式中的元素对象*****/
string element()
{
	if(shuru[ip]=="expr"||shuru[ip]=="num")
	{
		ip++;
		return shuru[ip-1];
	}
	else if(shuru[ip]=="(")
	{
		ip++;
		string result=biaodashi();
		if(shuru[ip]==")")ip++;
		else puts("Lack)");
		return result;
	}
	else puts("error");
	return "";
} 

/*****处理表达式*****/
string expression_1(string &op)
{
	if(shuru[ip]=="*"||shuru[ip]=="/")
	{
		op=shuru[ip];
		ip++;
		string arg1=element();
		string op_1="",result=l("t",tID++);
		string arg2=expression_1(op_1);
		if(op_1=="")op_1="=";
		if(arg2=="") cout<<address++<<":"<<" "<<result<<" = "<<arg1<<endl;
		else cout<<address++<<":"<<" "<<result<<" = "<<arg1<<" "<<op_1<<" "<<arg2<<endl;
		return result;
	}
	return "";
} 

/*****处理表达式*****/
string expression()
{
	string op="",result=l("t",tID++);
	string arg1=element();
	string arg2=expression_1(op);
	if(op=="")
	{
		op="=";
	} 
	if(arg2=="")
	{
		cout<<address++<<":"<<" "<<result<<" = "<<arg1<<endl;
	}
	else
	{
		cout<<address++<<":"<<" "<<result<<" = "<<arg1<<" "<<op<<" "<<arg2<<endl;
	}
	return result;
} 

/*****递归---处理表达式，转为三地址输出*****/
string biaodashi_1(string &op)
{
	string result="";
	if(shuru[ip]=="+"||shuru[ip]=="-")
	{
		op=shuru[ip];
		ip++;
		string arg1=expression();
		string op_1="";
		string arg2=biaodashi_1(op_1);
		result=l("t",tID++);
		if(op_1=="")
		{
			op_1="=";
		}
		if(arg2=="")
		{
			cout<<address++<<":"<<" "<<result<<" = "<<arg1<<endl;
		}
		else
		{
			cout<<address++<<":"<<" "<<result<<" = "<<arg1<<" "<<op_1<<" "<<arg2<<endl;
		}
	}
	return result;
} 

/*****处理表达式，转为三地址输出*****/
string biaodashi()
{
	string arg1="",op="";
	if(shuru[ip]=="+"||shuru[ip]=="-"){arg1=shuru[ip];ip++;}
	arg1+=expression();
	string arg2=biaodashi_1(op);
	string result=l("t",tID++);
	if(op=="")
	{
		op="=";
	}
	if(arg2=="")
	{
		cout<<address++<<":"<<" "<<result<<" = "<<arg1<<endl;
	}
	else
	{
		cout<<address++<<":"<<" "<<result<<" = "<<arg1<<" "<<op<<" "<<arg2<<endl;	
	}
	return result;
}

/*****判断并获取运算符*****/
string getOperator()
{
	if(shuru[ip]=="="||shuru[ip]=="<>"||shuru[ip]=="<"||shuru[ip]==">"||
		shuru[ip]=="<="||shuru[ip]==">=")
	{
		ip++;
		return shuru[ip-1];
	}
	else
	{
		puts("error");
	} 
	return "";
}

/*****输出if语句的条件的三地址代码*****/
void con(int L1,int L2)      //L1,L2分别为if条件为true和false时候的跳转地址 
{
	string result=l("t",tID++);
		string arg1=biaodashi();           //获得表达式的运算符的左边内容 
		string op=getOperator();		   //获得表达式的运算符 
		string arg2=biaodashi();		   //获得表达式的运算符的右边内容 
		if(arg2=="")
		{
			cout<<" "<<result<<" = "<<arg1<<endl;
		}
		else
		{
			cout<<address++<<":"<<" "<<result<<" = "<<arg1<<" "<<op<<" "<<arg2<<endl;
		}
		cout<<address++<<":"<<" if true "<<result<<" goto "<<"L"<<L1<<endl;
		cout<<address++<<":"<<" if false "<<result<<" goto "<<"L"<<L2<<endl;
}

/*****判断关键字，调用相应的产生式分析*****/
void yuyifenxi(int next,int &flag) 
{
	if(shuru[ip]=="expr")
	{
		ip++;
		if(shuru[ip]=="=")			//赋值语句 转化为四元式
		{
			ip++;
			string arg1=biaodashi();
			string arg2="";
			if(arg2 == "") cout<<address++<<":"<<" expr = "<<arg1<<endl;
		}
		else puts("error");
	}
	
	else if(shuru[ip]=="if") 		 //if的语义子程序
	{
		ip++;
		int L1=LID++;
		int L2=LID++;
		if(shuru[ip]=="(")
		{
			ip++;
			con(L1,L2);
		}
		else
		{
			puts("Lack(");return;
		}
		if(shuru[ip]==")") ip++;
		else {
			puts("Lack)");return;
		}
		printf("L%d:\n",L1);
		yuyifenxi(next,flag);
		ip++;
		if(shuru[ip]=="else")
		{
			printf("L%d:\n",L2);
			ip++;
			yuyifenxi(next,flag);
		}
	}
	else if(shuru[ip]=="while")			//while的语义子程序 
	{
		ip++;
		int L1=LID++;
		int L2=LID++;
		if(shuru[ip]=="(")
		{
			ip++;
			printf("L%d:\n",L1);
			con(L2,next);
		}
		else
		{
			puts("Lack(");return;
		}
		if(shuru[ip]==")") ip++;
		else {
			puts("Lack)");return;
		}
		printf("L%d:\n",L2);
		yuyifenxi(next,flag);
		printf("goto L%d\n",L1);
		flag=1;
	}
}

/*****递归---生成并输出条件返回地址*****/
void analysis()
{
	if(shuru[ip]==";")
	{
		ip++;
		int next=LID++;
		int flag=0;
		yuyifenxi(next,flag);
		if(flag)printf("L%d:\n",next);
		analysis();
	}
}

/*****生成并输出条件返回地址*****/
void analysis_list()
{
	int next=LID++;
	int flag=0;
	yuyifenxi(next,flag);
	if(flag)printf("L%d:\n",next);
	analysis();
}

void Modular()
{
	int next=LID++;
	int flag=0;
	analysis_list();
	if(flag)printf("L%d:\n",next);
}

/*****文件读入*****/
void readfile() 				//将字符串输入到shuru中
{
	maxsize=0;
	while(cin>>shuru[maxsize])
		maxsize++;
}

/*****主函数*****/
int main()
{
	freopen("input.txt","r",stdin);
	cout<<"语义分析如下："<<endl; 
	readfile();
	Modular();
	return 0;
}

