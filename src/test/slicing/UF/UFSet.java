package test.slicing.UF;
  
class UFSets{
	int parent[];
	int rank[];
	int SZ;
	
	UFSets(){
		SZ=20;
		parent=new int[SZ];
		rank=new int[SZ];
	}

	void MakeSet(int x){
		parent[x]=x;
		rank[x]=0;
	}

	int FindSet(int x){
		if(x!=parent[x]) parent[x]=FindSet(parent[x]);
		return parent[x];
	}

	void Link(int root1,int root2){
		if(root1==root2) return;
		if(rank[root1]>rank[root2]){
			parent[root2]=root1;
		}
		else{
			parent[root1]=root2;
			if(rank[root1]==rank[root2])
				rank[root2]++;
		}
	}
	void Union(int x,int y){
		Link(FindSet(x),FindSet(y));
	}
}
