/**
 *
 * @ProjectName JunitTestWithActualLib
 *
 * @PackageName callgraphstat
 *
 * @FileName WorldClass.java
 * 
 * @FileCreated Nov 2, 2013
 *
 * @Author MD. SHOHEL SHAMIM
 *
 * @CivicRegistration 19841201-0533
 *
 * MSc. in Software Technology
 *
 * Linnaeus University, Växjö, Sweden
 *
 */
package callgraphstat.testclasses;

class WorldClass {
    Sec sec = new Sec();
    WorldClass wc = new WorldClass();

    public void methodOne() {
	Sec s = new Sec();
	s.nurse("SD", new WorldClass());
	new Sec().dck();
	Another.What();
	BBC bbc = new Another();
	bbc.Nothing();
	new Another().Ne(new Sec());
	Sec ss = new Sec(new Sec());
	BBC b = ano();
	Another a = getAnother();
	getAnother();
	Sec sss = s;
    }

    public Another getAnother() {
	return new Another();
    }

    public void testing() {

    }

    public void methodTwo() {
	this.sec.dck();
    }

    public Another ano() {
	return new Another();
    }

    public WorldClass returnOwnClass() {
	WorldClass w = new WorldClass();
	w.ano();
	Another an = null;
	Sec sec = null;
	int a = 1;
	if (a == 2) {
	    an = new Another();
	} else {
	    sec = new Sec();
	}
	int c = 0;
	while (c > 5) {
	    new WorldClass();
	    c++;
	}
	return w;
    }
}

class Another implements BBC {
    public static void What() {

    }

    public void Ne(Sec s) {

    }

    @Override
    public void Nothing() {
    }
}

interface BBC {
    public void Nothing();
}