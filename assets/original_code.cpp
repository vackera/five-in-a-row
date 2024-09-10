
class c_amoba {
	private :
		char      tabla[20][20];
		int				kezdo;
		int				vege;
		int				hang;
		int				geppont;
		int       embpont;
		void 	pitty(void) { sound(1000);delay(2);nosound(); };
		void 	ikon(int,int,int,int);
		void 	optimalis(int&,int&);
		int 	ellenorzes(void);
		void  pontki(void);
	public  :
		c_amoba(int);
		void 	c_amoba::init(void);
		void 	hangkibe(void) { hang=!hang; };
		int		gepkezd(void) { return(kezdo); };
		void 	kezdesvaltas(void) { kezdo=!kezdo; };
		int 	vegevan(void) { return (vege); };
		void 	keret(void);
		int 	jatekoslep(int,int);
		void 	geplep(int);
};
void c_amoba::ikon(int x,int y,int tip,int szin) {
	setcolor(szin);
	setlinestyle(0,0,THICK_WIDTH);
	switch (tip) {
		case 'x' : {
			line(x*30+4,y*30+4,x*30+26,y*30+26);
			line(x*30+4,y*30+26,x*30+26,y*30+4);
		} break;
		case 'o' : {
			circle(x*30+15,y*30+15,11);
		} break;
	}
}

void c_amoba::optimalis(int &x,int &y) {
	#define 		_5os		10000
	#define 		_4es		1000
	#define 		_3as		100
	#define 		_2es		20

	#define 		e_5os		10000
	#define 		e_4es		1000
	#define 		e_3as		100
	#define 		e_2es		20

	#define			arany		20  //10
	#define			earany	15  //5

	int			a,b,c,v=0,u=0,k=0;
	int			vfok[20][20];					//15*15 ?
	int			efok[20][20];

	for (a=0;a<15;a++) {
		for (b=0;b<15;b++) {
			vfok[a][b]=0;
			efok[a][b]=0;
		}
	}
	for (a=0;a<15;a++) {
		for (b=0;b<15;b++) {
			if (b<11) {															//5-os lehetosegek keresese
				v=u=k=0;                                //vizszintes
				for (c=0;c<5;c++) {
				 if (tabla[a][b+c]=='x') v++;
				 else if (tabla[a][b+c]==' ') u++;
				 else k++;
				}
				if (v==4 && u==1) {
					for (c=0;tabla[a][b+c]!=' ';c++) ;
					vfok[a][b+c]+=_5os;
				}
				if (k==4 && u==1) {
					for (c=0;tabla[a][b+c]!=' ';c++) ;
					efok[a][b+c]+=e_5os;
				}
			}
			if (b<12) {                             //4-es lehetosegek keresese
				v=u=k=0;
				for (c=0;c<4;c++) {
				 if (tabla[a][b+c]=='x') v++;
				 else if (tabla[a][b+c]==' ') u++;
				 else k++;
				}
				if (v==3 && u==1) {
					for (c=0;tabla[a][b+c]!=' ';c++) ;
					if (b-1<0 || b+4>14 || tabla[a][b-1]=='o' || tabla[a][b+4]=='o')
						vfok[a][b+c]+=_4es/arany;
					else
						vfok[a][b+c]+=_4es;
				}
				if (k==3 && u==1) {
					for (c=0;tabla[a][b+c]!=' ';c++) ;
					if (b-1<0 || b+4>14 || tabla[a][b-1]=='x' || tabla[a][b+4]=='x')
						efok[a][b+c]+=e_4es/earany;
					else
						efok[a][b+c]+=e_4es;
				}
			}
			if (b<13) {                             //3-as lehetosegek keresese
				v=u=k=0;
				for (c=0;c<3;c++) {
				 if (tabla[a][b+c]=='x') v++;
				 else if (tabla[a][b+c]==' ') u++;
				 else k++;
				}
				if (v==2 && u==1) {
					for (c=0;tabla[a][b+c]!=' ';c++) ;
					if (b-1<0 || b+3>14 || tabla[a][b-1]=='o' || tabla[a][b+3]=='o')
						vfok[a][b+c]+=_3as/arany;
					else
						vfok[a][b+c]+=_3as;
				}
				if (k==2 && u==1) {
					for (c=0;tabla[a][b+c]!=' ';c++) ;
					if (b-1<0 || b+3>14 || tabla[a][b-1]=='x' || tabla[a][b+3]=='x')
						efok[a][b+c]+=e_3as/earany;
					else
						efok[a][b+c]+=e_3as;
				}
			}
			if (b<14) {                             //2-es lehetosegek keresese
				v=u=k=0;
				for (c=0;c<2;c++) {
				 if (tabla[a][b+c]=='x') v++;
				 else if (tabla[a][b+c]==' ') u++;
				 else k++;
				}
				if (v==1 && u==1) {
					for (c=0;tabla[a][b+c]!=' ';c++) ;
					if (b-1<0 || b+2>14 || tabla[a][b-1]=='o' || tabla[a][b+2]=='o')
						vfok[a][b+c]+=_2es/arany;
					else
						vfok[a][b+c]+=_2es;
				}
				if (k==1 && u==1) {
					for (c=0;tabla[a][b+c]!=' ';c++) ;
					if (b-1<0 || b+2>14 || tabla[a][b-1]=='x' || tabla[a][b+2]=='x')
						efok[a][b+c]+=e_2es/earany;
					else
						efok[a][b+c]+=e_2es;
				}
			}

			if (a<11) {															//5-os lehetosegek keresese
				v=u=k=0;                                //fuggoleges
				for (c=0;c<5;c++) {
				 if (tabla[a+c][b]=='x') v++;
				 else if (tabla[a+c][b]==' ') u++;
				 else k++;
				}
				if (v==4 && u==1) {
					for (c=0;tabla[a+c][b]!=' ';c++) ;
					vfok[a+c][b]+=_5os;
				}
				if (k==4 && u==1) {
					for (c=0;tabla[a+c][b]!=' ';c++) ;
					efok[a+c][b]+=e_5os;
				}
			}
			if (a<12) {                             //4-es lehetosegek keresese
				v=u=k=0;
				for (c=0;c<4;c++) {
				 if (tabla[a+c][b]=='x') v++;
				 else if (tabla[a+c][b]==' ') u++;
				 else k++;
				}
				if (v==3 && u==1) {
					for (c=0;tabla[a+c][b]!=' ';c++) ;
					if (a-1<0 || a+4>14 || tabla[a-1][b]=='o' || tabla[a+4][b]=='o')
						vfok[a+c][b]+=_4es/arany;
					else
						vfok[a+c][b]+=_4es;
				}
				if (k==3 && u==1) {
					for (c=0;tabla[a+c][b]!=' ';c++) ;
					if (a-1<0 || a+4>14 || tabla[a-1][b]=='x' || tabla[a+4][b]=='x')
						efok[a+c][b]+=e_4es/earany;
					else
						efok[a+c][b]+=e_4es;
				}
			}
			if (a<13) {                             //3-as lehetosegek keresese
				v=u=k=0;
				for (c=0;c<3;c++) {
				 if (tabla[a+c][b]=='x') v++;
				 else if (tabla[a+c][b]==' ') u++;
				 else k++;
				}
				if (v==2 && u==1) {
					for (c=0;tabla[a+c][b]!=' ';c++) ;
					if (a-1<0 || a+3>14 || tabla[a-1][b]=='o' || tabla[a+3][b]=='o')
						vfok[a+c][b]+=_3as/arany;
					else
						vfok[a+c][b]+=_3as;
				}
				if (k==2 && u==1) {
					for (c=0;tabla[a+c][b]!=' ';c++) ;
					if (a-1<0 || a+3>14 || tabla[a-1][b]=='x' || tabla[a+3][b]=='x')
						efok[a+c][b]+=e_3as/earany;
					else
						efok[a+c][b]+=e_3as;
				}
			}
			if (a<14) {                             //2-es lehetosegek keresese
				v=u=k=0;
				for (c=0;c<2;c++) {
				 if (tabla[a+c][b]=='x') v++;
				 else if (tabla[a+c][b]==' ') u++;
				 else k++;
				}
				if (v==1 && u==1) {
					for (c=0;tabla[a+c][b]!=' ';c++) ;
					if (a-1<0 || a+2>14 || tabla[a-1][b]=='o' || tabla[a+2][b]=='o')
						vfok[a+c][b]+=_2es/arany;
					else
						vfok[a+c][b]+=_2es;
				}
				if (k==1 && u==1) {
					for (c=0;tabla[a+c][b]!=' ';c++) ;
					if (a-1<0 || a+2>14 || tabla[a-1][b]=='x' || tabla[a+2][b]=='x')
						efok[a+c][b]+=e_2es/earany;
					else
						efok[a+c][b]+=e_2es;
				}
			}

			if (a<11 && b<11) {											//5-os lehetosegek keresese
				v=u=k=0;                                //atlo : "\"
				for (c=0;c<5;c++) {
				 if (tabla[a+c][b+c]=='x') v++;
				 else if (tabla[a+c][b+c]==' ') u++;
				 else k++;
				}
				if (v==4 && u==1) {
					for (c=0;tabla[a+c][b+c]!=' ';c++) ;
					vfok[a+c][b+c]+=_5os;
				}
				if (k==4 && u==1) {
					for (c=0;tabla[a+c][b+c]!=' ';c++) ;
					efok[a+c][b+c]+=e_5os;
				}
			}
			if (a<12 && b<12) {                     //4-es lehetosegek keresese
				v=u=k=0;
				for (c=0;c<4;c++) {
				 if (tabla[a+c][b+c]=='x') v++;
				 else if (tabla[a+c][b+c]==' ') u++;
				 else k++;
				}
				if (v==3 && u==1) {
					for (c=0;tabla[a+c][b+c]!=' ';c++);
					if (a-1<0 || b-1<0 || a+4>14 || b+4>14
							|| tabla[a-1][b-1]=='o' || tabla[a+4][b+4]=='o')
						vfok[a+c][b+c]+=_4es/arany;
					else
						vfok[a+c][b+c]+=_4es;
				}
				if (k==3 && u==1) {
					for (c=0;tabla[a+c][b+c]!=' ';c++) ;
					if (a-1<0 || b-1<0 || a+4>14 || b+4>14
							|| tabla[a-1][b-1]=='x' || tabla[a+4][b+4]=='x')
						efok[a+c][b+c]+=e_4es/earany;
					else
						efok[a+c][b+c]+=e_4es;
				}
			}
			if (a<13 && b<13) {                     //3-as lehetosegek keresese
				v=u=k=0;
				for (c=0;c<3;c++) {
				 if (tabla[a+c][b+c]=='x') v++;
				 else if (tabla[a+c][b+c]==' ') u++;
				 else k++;
				}
				if (v==2 && u==1) {
					for (c=0;tabla[a+c][b+c]!=' ';c++) ;
					if (a-1<0 || b-1<0 || a+3>14 || b+3>14
							|| tabla[a-1][b-1]=='o' || tabla[a+3][b+3]=='o')
						vfok[a+c][b+c]+=_3as/arany;
					else
						vfok[a+c][b+c]+=_3as;
				}
				if (k==2 && u==1) {
					for (c=0;tabla[a+c][b+c]!=' ';c++) ;
					if (a-1<0 || b-1<0 || a+3>14 || b+3>14
							|| tabla[a-1][b-1]=='x' || tabla[a+3][b+3]=='x')
						efok[a+c][b+c]+=e_3as/earany;
					else
						efok[a+c][b+c]+=e_3as;
				}
			}
			if (a<14 && b<14) {                     //2-es lehetosegek keresese
				v=u=k=0;
				for (c=0;c<2;c++) {
				 if (tabla[a+c][b+c]=='x') v++;
				 else if (tabla[a+c][b+c]==' ') u++;
				 else k++;
				}
				if (v==1 && u==1) {
					for (c=0;tabla[a+c][b+c]!=' ';c++) ;
					if (a-1<0 || b-1<0 || a+2>14 || b+2>14
							|| tabla[a-1][b-1]=='o' || tabla[a+2][b+2]=='o')
						vfok[a+c][b+c]+=_2es/arany;
					else
						vfok[a+c][b+c]+=_2es;
				}
				if (k==1 && u==1) {
					for (c=0;tabla[a+c][b+c]!=' ';c++) ;
					if (a-1<0 || b-1<0 || a+2>14 || b+2>14
							|| tabla[a-1][b-1]=='x' || tabla[a+2][b+2]=='x')
						efok[a+c][b+c]+=e_2es/earany;
					else
						efok[a+c][b+c]+=e_2es;
				}
			}

			if (a>3 && b<11) {											//5-os lehetosegek keresese
				v=u=k=0;                                //atlo : "/"
				for (c=0;c<5;c++) {
				 if (tabla[a-c][b+c]=='x') v++;
				 else if (tabla[a-c][b+c]==' ') u++;
				 else k++;
				}
				if (v==4 && u==1) {
					for (c=0;tabla[a-c][b+c]!=' ';c++) ;
					vfok[a-c][b+c]+=_5os;
				}
				if (k==4 && u==1) {
					for (c=0;tabla[a-c][b+c]!=' ';c++) ;
					efok[a-c][b+c]+=e_5os;
				}
			}
			if (a>2 && b<12) {                      //4-es lehetosegek keresese
				v=u=k=0;
				for (c=0;c<4;c++) {
				 if (tabla[a-c][b+c]=='x') v++;
				 else if (tabla[a-c][b+c]==' ') u++;
				 else k++;
				}
				if (v==3 && u==1) {
					for (c=0;tabla[a-c][b+c]!=' ';c++) ;
					if (a+1>14 || b-1<0 || a-4<0 || b+4>14
							|| tabla[a+1][b-1]=='o' || tabla[a-4][b+4]=='o')
						vfok[a-c][b+c]+=_4es/arany;
					else
						vfok[a-c][b+c]+=_4es;
				}
				if (k==3 && u==1) {
					for (c=0;tabla[a-c][b+c]!=' ';c++) ;
					if (a+1>14 || b-1<0 || a-4<0 || b+4>14
							|| tabla[a+1][b-1]=='x' || tabla[a-4][b+4]=='x')
						efok[a-c][b+c]+=e_4es/earany;
					else
						efok[a-c][b+c]+=e_4es;
				}
			}
			if (a>1 && b<13) {                      //3-as lehetosegek keresese
				v=u=k=0;
				for (c=0;c<3;c++) {
				 if (tabla[a-c][b+c]=='x') v++;
				 else if (tabla[a-c][b+c]==' ') u++;
				 else k++;
				}
				if (v==2 && u==1) {
					for (c=0;tabla[a-c][b+c]!=' ';c++) ;
					if (a+1>14 || b-1<0 || a-3<0 || b+3>14
							|| tabla[a+1][b-1]=='o' || tabla[a-3][b+3]=='o')
						vfok[a-c][b+c]+=_3as/arany;
					else
						vfok[a-c][b+c]+=_3as;
				}
				if (k==2 && u==1) {
					for (c=0;tabla[a-c][b+c]!=' ';c++) ;
					if (a+1>14 || b-1<0 || a-3<0 || b+3>14
							|| tabla[a+1][b-1]=='x' || tabla[a-3][b+3]=='x')
						efok[a-c][b+c]+=e_3as/earany;
					else
						efok[a-c][b+c]+=e_3as;
				}
			}
			if (a>0 && b<14) {                      //2-es lehetosegek keresese
				v=u=k=0;
				for (c=0;c<2;c++) {
				 if (tabla[a-c][b+c]=='x') v++;
				 else if (tabla[a-c][b+c]==' ') u++;
				 else k++;
				}
				if (v==1 && u==1) {
					for (c=0;tabla[a-c][b+c]!=' ';c++) ;
					if (a+1>14 || b-1<0 || a-2<0 || b+2>14
							|| tabla[a+1][b-1]=='o' || tabla[a-2][b+2]=='o')
						vfok[a-c][b+c]+=_2es/arany;
					else
						vfok[a-c][b+c]+=_2es;
				}
				if (k==1 && u==1) {
					for (c=0;tabla[a-c][b+c]!=' ';c++) ;
					if (a+1>14 || b-1<0 || a-2<0 || b+2>14
							|| tabla[a+1][b-1]=='x' || tabla[a-2][b+2]=='x')
						efok[a-c][b+c]+=e_2es/earany;
					else
						efok[a-c][b+c]+=e_2es;
				}
			}
		}
	}

	struct {int x,y;} legv={0,0},lege={0,0},lego={0,0},legok[9];
	int 					vmax=0,emax=0,omax=0;
	int						oszam=0;
	int						ofok[20][20];
	for (a=0;a<15;a++) {
		for (b=0;b<15;b++) {
			ofok[a][b]=vfok[a][b]+efok[a][b];
		}
	}
	for (a=0;a<15;a++) {
		for (b=0;b<15;b++) {
			if (vfok[a][b]>vmax) {
				vmax=vfok[a][b];
				legv.x=b;
				legv.y=a;
			}
			if (efok[a][b]>emax) {
				emax=efok[a][b];
				lege.x=b;
				lege.y=a;
			}
			if (ofok[a][b]>omax) {
				oszam=0;
				omax=ofok[a][b];
				legok[0].x=b;
				legok[0].y=a;
			}
			if (ofok[a][b]==omax && oszam<8) {
				oszam++;
				legok[oszam].x=b;
				legok[oszam].y=a;
			}
		}
	}
	a=rand()%(oszam+1);
	lego.x=legok[a].x;
	lego.y=legok[a].y;

	if (efok[lege.y][lege.x]>=e_5os) {
		x=lege.x;
		y=lege.y;
	}	else if (vfok[legv.y][legv.x]>=_5os) {
		x=legv.x;
		y=legv.y;
	} else if (vfok[legv.y][legv.x]>=_4es && efok[lege.y][lege.x]>=e_4es) {
		x=lege.x;
		y=lege.y;
	} else {
		x=lego.x;
		y=lego.y;
	}
}
int c_amoba::ellenorzes(void) {
	int			a,b,c,veg=0;
	int			ch;

	for (a=0;a<15 && veg==0;a++) {
		for (b=0;b<15 && veg==0;b++) {
			if (tabla[a][b]!=' ') {
				ch=tabla[a][b];
				veg=1;
				for (c=0;c<5;c++) {
					if (tabla[a][b+c]!=ch || b+c>14) veg=0;
				}
				if (veg==1) {
					setcolor(4);
					for (c=0;c<5;c++) ikon(b+c,a,ch,4);
					continue;
				}
				veg=1;
				for (c=0;c<5;c++) {
					if (tabla[a+c][b]!=ch || a+c>14) veg=0;
				}
				if (veg==1) {
					setcolor(4);
					for (c=0;c<5;c++) ikon(b,a+c,ch,4);
					continue;
				}
				veg=1;
				for (c=0;c<5;c++) {
					if (tabla[a+c][b+c]!=ch || a+c>14 || b+c>14) veg=0;
				}
				if (veg==1) {
					setcolor(4);
					for (c=0;c<5;c++) ikon(b+c,a+c,ch,4);
					continue;
				}
				veg=1;
				for (c=0;c<5;c++) {
					if (tabla[a-c][b+c]!=ch || a-c<0 || b+c>14) veg=0;
				}
				if (veg==1) {
					setcolor(4);
					for (c=0;c<5;c++) ikon(b+c,a-c,ch,4);
					continue;
				}
			}
		}
	}
	if (veg==1) {
		if (ch=='x') embpont++;
			else			 geppont++;
		pontki();
	}
	if (veg==0) {
		veg=2;
		for (a=0;a<15;a++) {
			for (b=0;b<15;b++) {
				if (tabla[a][b]==' ') veg=0;
			}
		}
	}
	return(vege=veg);
}
void c_amoba::pontki(void) {
	char			s[15];
	setfillstyle(1,0);
	bar(460,0,640,480);
	settextstyle(1,0,4);
	settextjustify(1,1);
	setcolor(15);
	sprintf(s,"Gep");
	outtextxy(545,20,s);
	sprintf(s,"%d",geppont);
	outtextxy(545,70,s);
	setcolor(11);
	sprintf(s,"Jatekos");
	outtextxy(545,380,s);
	sprintf(s,"%d",embpont);
	outtextxy(545,430,s);
}
c_amoba::c_amoba(int kezd=0) {
	randomize();
	init();
	hang=1;
	kezdo=kezd;
	geppont=embpont=0;
}
void c_amoba::init(void) {
	int			a,b;
	for (a=0;a<20;a++) {
		for (b=0;b<20;b++) {
			tabla[a][b]=' ';
		}
	}
	vege=0;
}
void c_amoba::keret(void) {
	int			a,b;
	setcolor(2);
	setlinestyle(0,0,NORM_WIDTH);
	for (a=0;a<15;a++) {
		for (b=0;b<15;b++) {
			rectangle(b*30,a*30,b*30+30,a*30+30);
		}
	}
	pontki();
}
int c_amoba::jatekoslep(int x,int y) {
	int			xx,yy;
	xx=x/30;
	yy=y/30;
	if (tabla[yy][xx]==' ') {
		egerkikapcs();
		tabla[yy][xx]='x';
		if (hang) pitty();
		ikon(xx,yy,'x',11);
		ellenorzes();
		egerbekapcs();
		return(1);
	} else return(0);
}
void c_amoba::geplep(int gkezd=0) {
	int		gx,gy;
	if (gkezd) {
		gx=7;
		gy=7;
	}	else {
		optimalis(gx,gy);
	}
	tabla[gy][gx]='o';
	egerkikapcs();
	ikon(gx,gy,'o',15);
	ellenorzes();
	egerbekapcs();
}
//-------------------------------------------------------------------------//

//-------------------------------------------------------------------------//
class c_amoba_jatek {
	public :
		int		inditas(void);
};
int	c_amoba_jatek::inditas(void) {
	int					gd=VGA,gm=VGAHI;initgraph(&gd,&gm,"");
	if (!egerinit()) {
		settextstyle(1,0,5);
		settextjustify(1,1);
		setcolor(4);
		outtextxy(320,240,"A JATEKHOZ EGER SZUKSEGES !");
		getch();
		closegraph();
		return(0);
	}
	int					k=0;
	int					x,y,gombok;
	c_amoba			amoba(0);

	do {
		cleardevice();
		amoba.init();
		amoba.keret();
		egerablak(0,0,449,449);
		egerbekapcs();
		if (amoba.gepkezd()) amoba.geplep(1);
		do {
			egerinfo(x,y,gombok);
			if (gombok==1) {
				if (amoba.jatekoslep(x,y) && !amoba.vegevan()) amoba.geplep();
				delay(300);
			}
			if (kbhit()) {
				k=getch();
				if (k==0) {
					k=getch();
					if (k==31) amoba.hangkibe();
				}
			}
		} while (k!=27 && !amoba.vegevan());
		if (amoba.vegevan()) {
			do {
				egerinfo(x,y,gombok);
			} while (!kbhit() && gombok==0);
			if (kbhit()) k=getch();
				else delay(300);
		}
		amoba.kezdesvaltas();
		egerkikapcs();
	} while (k!=27);
	closegraph();
	return(1);
}