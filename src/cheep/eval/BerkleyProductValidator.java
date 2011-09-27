package cheep.eval;

import static org.junit.Assert.assertTrue;

import java.lang.ref.Reference;
import java.util.ArrayList;

import org.junit.Test;

import cheep.model.Product;
import cheep.model.ProductSet;

public class BerkleyProductValidator extends ProductValidator{
	
	/**
%OPT - SPL
0: [LOGGING_FINER]
1: [LOGGING_CONFIG]
2: [LOGGING_SEVERE]
3: [LOGGING_EVICTOR]
4: [LOGGING_CLEANER]
5: [LOGGING_RECOVERY]
6: [LOGGING_DBLOGHANDLER]
7: [LOGGING_CONSOLEHANDLER]
8: [LOGGING_INFO]
9: LOGGING_BASE 
10: [LOGGING_FILEHANDLER]
11: [LOGGING_FINE]
12: [LOGGING_FINEST]
%ENDOPT - SPL
% - ConcurrTrans 
13: [LATCHES]
14: TRANSACTIONS
15: [CHECKLEAKS]
16: [FSYNC];
%END concurrtrans

% - Persistance 
17: [CHECKSUM]

%CHOOSE - OLDIO or NEWIO
% - IIO 
  % - OldIO
  	18: [SYNCHRONIZEDIO] 
  	19: IO
  %END OldIO
  % - NewIO
  	% - NIOAccess
	  20: [CHUNCKEDNIO] 
	  21: NIO
	%END NIOAccess
	22: [DIRECTNIO]
  %END NewIO
%END IIO
%END choose

23: [ENVIRONMENT_LOCKING] 

% - Checkpointer
  24: [CP_BYTES] 
  25: [CP_TIME]
  26: [CHECKPOINTER_DAEMON]
%END checkpointer

27: [DISKFULLERRO]
28: [FILEHANDLECACHE]

% - IICLEANER
29: [CLEANERDAEMON] 
30: CLEANER
31: [LOOKAHEADCACHE]
%END IICLEANER

%END persistance

32: [STATISTICS]

% - BTree
33: [INCOMPRESSOR]
%OPT - [IEVICTOR]
   34: [CRITICAL_EVICTION]
     35: [EVICTORDAEMON] 
   36: EVICTOR
%END OPT IEVICTOR
37: [VERIFIER]
%END Btree

% - Ops
38: [DELETEOP] 
39: [RENAMEOP] 
40: [TRUNCATEOP]
%END - OPS
41: [MEMORY_BUDGET];
	 */
	
	public static final String[] reference = new String[] { "LOGGING_FINER",
			"LOGGING_CONFIG", "LOGGING_SEVERE", "LOGGING_EVICTOR",
			"LOGGING_CLEANER", "LOGGING_RECOVERY", "LOGGING_DBLOGHANDLER",
			"LOGGING_CONSOLEHANDLER", "LOGGING_INFO", "LOGGING_BASE",
			"LOGGING_FILEHANDLER", "LOGGING_FINE", "LOGGING_FINEST", "LATCHES",
			"TRANSACTIONS", "CHECKLEAKS", "FSYNC", "CHECKSUM",
			"SYNCHRONIZEDIO", "IO", "CHUNCKEDNIO", "NIO", "DIRECTNIO",
			"ENVIRONMENT_LOCKING", "CP_BYTES", "CP_TIME",
			"CHECKPOINTER_DAEMON", "DISKFULLERRO", "FILEHANDLECACHE",
			"CLEANERDAEMON", "CLEANER", "LOOKAHEADCACHE", "STATISTICS",
			"INCOMPRESSOR", "CRITICAL_EVICTION", "EVICTORDAEMON", "EVICTOR",
			"VERIFIER", "DELETEOP", "RENAMEOP", "TRUNCATEOP", "MEMORY_BUDGET" };
	
	@Override
	public boolean validate(Product p) {
		boolean[] fts = p.getFeatures();
		
		boolean isValid = true;

		boolean LOGGING_FINER= fts[0];
		boolean LOGGING_CONFIG= fts[1];
		boolean LOGGING_SEVERE= fts[2];
		boolean LOGGING_EVICTOR= fts[3];
		boolean LOGGING_CLEANER= fts[4];
		boolean LOGGING_RECOVERY= fts[5];
		boolean LOGGING_DBLOGHANDLER= fts[6];
		boolean LOGGING_CONSOLEHANDLER= fts[7];
		boolean LOGGING_INFO= fts[8];
		boolean LOGGING_BASE = fts[9];
		boolean LOGGING_FILEHANDLER= fts[10];
		boolean LOGGING_FINE= fts[11];
		boolean LOGGING_FINEST= fts[12];
		boolean LATCHES= fts[13];
		boolean TRANSACTIONS= fts[14];
		boolean CHECKLEAKS= fts[15];
		boolean FSYNC= fts[16];
		boolean CHECKSUM= fts[17];
		boolean SYNCHRONIZEDIO = fts[18];
		boolean IO= fts[19];
		boolean CHUNCKEDNIO = fts[20];
		boolean NIO= fts[21];
		boolean DIRECTNIO= fts[22];
		boolean ENVIRONMENT_LOCKING = fts[23];
		boolean CP_BYTES = fts[24];
		boolean CP_TIME= fts[25];
		boolean CHECKPOINTER_DAEMON= fts[26];
		boolean DISKFULLERRO= fts[27];
		boolean FILEHANDLECACHE= fts[28];
		boolean CLEANERDAEMON = fts[29];
		boolean CLEANER= fts[30];
		boolean LOOKAHEADCACHE= fts[31];
		boolean STATISTICS= fts[32];
		boolean INCOMPRESSOR= fts[33];
		boolean CRITICAL_EVICTION= fts[34];
		boolean EVICTORDAEMON = fts[35];
		boolean EVICTOR= fts[36];
		boolean VERIFIER= fts[37];
		boolean DELETEOP = fts[38];
		boolean RENAMEOP = fts[39];
		boolean TRUNCATEOP= fts[40];
		boolean MEMORY_BUDGET= fts[41];


		boolean LOGGING = (LOGGING_FINER || LOGGING_CONFIG || LOGGING_SEVERE || LOGGING_EVICTOR || LOGGING_CLEANER || LOGGING_RECOVERY || LOGGING_DBLOGHANDLER || LOGGING_CONSOLEHANDLER || LOGGING_INFO || LOGGING_BASE || LOGGING_FILEHANDLER || LOGGING_FINE || LOGGING_FINEST);
		
		if(LOGGING) {
			isValid &= LOGGING_BASE;
		}
		
		isValid &= TRANSACTIONS;
		
		boolean OLD_IO = (SYNCHRONIZEDIO || IO);
		boolean NEW_IO = (CHUNCKEDNIO || NIO || DIRECTNIO);
		
		isValid &= OLD_IO ^ NEW_IO;
		
		if(OLD_IO) {
			isValid &= IO;
		} else if (NEW_IO) {
			isValid &= CHUNCKEDNIO ^ NIO;
		}
		
		//all features of checkpointer are optional, so checkpointer is optional
		boolean Checkpointer = (CP_BYTES || CP_TIME || CHECKPOINTER_DAEMON);
		
		boolean IICLEANER = (CLEANERDAEMON || CLEANER || LOOKAHEADCACHE);
		isValid &= IICLEANER; //redundant
		isValid &= CLEANER; 
		
		boolean IEVICTOR = (CRITICAL_EVICTION || EVICTORDAEMON || EVICTOR);
		if(IEVICTOR) {
			isValid &= EVICTOR;
		}
		
		//all features of btree are optional
		boolean BTREE = (INCOMPRESSOR || IEVICTOR || VERIFIER);
//		isValid &= BTREE;
		
		//all features of ops are optional
		boolean OPS = (DELETEOP || RENAMEOP || TRUNCATEOP);
		
		/** Constraints **/
		
		if(LOOKAHEADCACHE) {
			isValid &= MEMORY_BUDGET;
		}
		
		if(EVICTORDAEMON) {
			isValid &= MEMORY_BUDGET;
		}
		
		if(EVICTOR) {
			isValid &= MEMORY_BUDGET;
		}
		
		if(CRITICAL_EVICTION) {
			isValid &= INCOMPRESSOR;
		}
		
		if(CP_BYTES) {
			isValid &= CP_TIME;
		}
		
		if(DELETEOP) {
			isValid &= EVICTOR;
			isValid &= MEMORY_BUDGET;
			isValid &= INCOMPRESSOR;
		}
		
		if(MEMORY_BUDGET) {
			isValid &= EVICTOR;
			isValid &= LATCHES;
		}
		
		if(TRUNCATEOP) {
			isValid &= DELETEOP;
		}
		
		if(VERIFIER) {
			isValid &= INCOMPRESSOR;
		}
		
		
		return isValid;
	}
	
	@Test
	public void testValidatorElton() {
		
		ProductValidator valP = new BerkleyProductValidator();
		Validator<ProductSet> valPset = new ProductSetValidator(valP, 0.9, 0.99);
		
//		LOGGING_FINER,LOGGING_CONFIG,LOGGING_SEVERE,LOGGING_EVICTOR,LOGGING_CLEANER,LOGGING_RECOVERY,LOGGING_DBLOGHANDLER,LOGGING_CONSOLEHANDLER,LOGGING_INFO,LOGGING_BASE,LOGGING_FILEHANDLER,LOGGING_FINE,LOGGING_FINEST,LATCHES,TRANSACTIONS,CHECKLEAKS,FSYNC,CHECKSUM,ENVIRONMENT_LOCKING,DISKFULLERRO,FILEHANDLECACHE,CP_BYTES,CP_TIME,CHECKPOINTER_DAEMON,CLEANERDAEMON,CLEANER,LOOKAHEADCACHE,STATISTICS,INCOMPRESSOR,VERIFIER,CRITICAL_EVICTION,EVICTORDAEMON,EVICTOR,DELETEOP,RENAMEOP,TRUNCATEOP,MEMORY_BUDGET,SYNCHRONIZEDIO,IO
		boolean[] b1 = new boolean[]{true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true};
		b1[20] = false;
		b1[21] = false;
		b1[22] = false;
		Product p1 = new Product(b1);
		assertTrue(b1.length == 42);
		assertTrue(valP.validate(p1));

//		LOGGING_FINER,LOGGING_CONFIG,LOGGING_SEVERE,LOGGING_EVICTOR,LOGGING_CLEANER,LOGGING_RECOVERY,LOGGING_DBLOGHANDLER,LOGGING_CONSOLEHANDLER,LOGGING_INFO,LOGGING_BASE,LOGGING_FILEHANDLER,LOGGING_FINE,LOGGING_FINEST,LATCHES,TRANSACTIONS,CHECKLEAKS,FSYNC,CHECKSUM,ENVIRONMENT_LOCKING,DISKFULLERRO,FILEHANDLECACHE,CP_BYTES,CP_TIME,CHECKPOINTER_DAEMON,CLEANERDAEMON,CLEANER,LOOKAHEADCACHE,STATISTICS,INCOMPRESSOR,VERIFIER,CRITICAL_EVICTION,EVICTORDAEMON,EVICTOR,DELETEOP,RENAMEOP,TRUNCATEOP,MEMORY_BUDGET,DIRECTNIO,CHUNCKEDNIO
		boolean[] b2 = new boolean[]{true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true};
		b2[18] = false;
		b2[19] = false;
		b2[21] = false;
		Product p2 = new Product(b2);
		assertTrue(b2.length == 42);
		assertTrue(valP.validate(p2));

//		LOGGING_FINER,LOGGING_CONFIG,LOGGING_SEVERE,LOGGING_EVICTOR,LOGGING_CLEANER,LOGGING_RECOVERY,LOGGING_DBLOGHANDLER,LOGGING_CONSOLEHANDLER,LOGGING_INFO,LOGGING_BASE,LOGGING_FILEHANDLER,LOGGING_FINE,LOGGING_FINEST,LATCHES,TRANSACTIONS,CHECKLEAKS,FSYNC,CHECKSUM,ENVIRONMENT_LOCKING,DISKFULLERRO,FILEHANDLECACHE,CP_BYTES,CP_TIME,CHECKPOINTER_DAEMON,CLEANERDAEMON,CLEANER,LOOKAHEADCACHE,STATISTICS,INCOMPRESSOR,VERIFIER,CRITICAL_EVICTION,EVICTORDAEMON,EVICTOR,DELETEOP,RENAMEOP,TRUNCATEOP,MEMORY_BUDGET,DIRECTNIO,NIO
		boolean[] b3 = new boolean[]{true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true};
		b3[18] = false;
		b3[19] = false;
		b3[20] = false;
		Product p3 = new Product(b3);
		assertTrue(b3.length == 42);
		assertTrue(valP.validate(p3));
		
		ProductSet pset = new ProductSet(new Product[]{p1,p2,p3});
		valPset.getFitness(pset, new ArrayList<ProductSet>());
	}
	
	@Test
	public void testValidatorCheepCheep() {
		
		ProductValidator valP = new BerkleyProductValidator();
		Validator<ProductSet> valPset = new ProductSetValidator(valP, 0.9, 0.99);
		
		boolean[] b1 = new boolean[]{false, true, true, true, true, true, true, false, false, true, true, true, true, true, true, true, true, false, false, false, false, true, false, true, false, false, false, false, false, false, true, false, false, false, false, true, true, false, false, true, false, true};
		System.out.println(print01(b1));
		Product p1 = new Product(b1);
		assertTrue(b1.length == 42);
		assertTrue(valP.validate(p1));

		boolean[] b2 = new boolean[]{true, true, true, true, false, true, false, true, true, true, false, true, true, true, true, false, false, true, false, false, true, false, false, true, true, true, false, false, true, true, true, false, false, true, true, false, true, false, true, true, true, true};
		System.out.println(print01(b2));
		Product p2 = new Product(b2);
		assertTrue(b2.length == 42);
		assertTrue(valP.validate(p2));

		boolean[] b3 = new boolean[]{false, true, false, true, false, false, true, true, true, true, true, false, false, true, true, false, false, true, false, false, true, false, true, true, true, true, false, false, false, true, true, true, true, true, true, true, true, true, true, true, false, true};
		System.out.println(print01(b3));
		Product p3 = new Product(b3);
		assertTrue(b3.length == 42);
		assertTrue(valP.validate(p3));
		
		boolean[] b4 = new boolean[]{false, false, true, true, false, false, false, true, false, true, false, true, true, true, true, false, true, true, true, true, false, false, false, false, false, false, true, true, false, true, true, false, true, true, true, true, true, false, false, true, false, true};
		System.out.println(print01(b4));
		Product p4 = new Product(b4);
		assertTrue(b4.length == 42);
		assertTrue(valP.validate(p4));

		
		ProductSet pset = new ProductSet(new Product[]{p1,p2,p3,p4});
		valPset.getFitness(pset, new ArrayList<ProductSet>());
	}

	public static String print01(boolean[] array) {
		StringBuffer sb = new StringBuffer();
		for (boolean b : array) {
			if(b) {
				sb.append(1);				
			} else {
				sb.append(0);
			}
		}
		return sb.toString();
	}

	public static String printProduct(boolean[] features) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < features.length; i++) {
			if(features[i]) {
				sb.append(reference[i]);
				sb.append(" ");
			} 
		}
		return sb.toString();
	}
	
	@Override
	public boolean[] xorCombination(Product t) {
		boolean[] f = t.getFeatures();
		boolean SYNCHRONIZEDIO = f[18];
		boolean IO = f[19];
		boolean CHUNCKEDNIO = f[20];
		boolean NIO= f[21];
		boolean DIRECTNIO = f[22];
		
		return new boolean[] {SYNCHRONIZEDIO,IO,CHUNCKEDNIO,NIO,DIRECTNIO};
	}
	
}
