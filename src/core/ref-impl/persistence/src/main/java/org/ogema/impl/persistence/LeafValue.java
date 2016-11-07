/**
 * This file is part of OGEMA.
 *
 * OGEMA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3
 * as published by the Free Software Foundation.
 *
 * OGEMA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OGEMA. If not, see <http://www.gnu.org/licenses/>.
 */
package org.ogema.impl.persistence;

import org.ogema.persistence.DBConstants;
import org.ogema.persistence.PersistencePolicy.ChangeInfo;
import org.ogema.resourcetree.SimpleResourceData;

public class LeafValue implements SimpleResourceData {

	// Fields holding the value of the leaf.
	// only one of them has an meaningful content.
	// typeKey specify which variable holds the valid value.
	volatile public boolean Z;// BoolenaResource
	volatile public float F;// FloatResource
	volatile public int I;// IntegerResource
	volatile public long J;// TimeResource
	volatile public String S;// StringResource

	volatile public boolean aZ[];// BoolenaArrayResource
	volatile public float aF[];// FloatArrayResource
	volatile public int aI[];// IntegerArrayResource
	volatile public long aJ[];// TimeArrayResource
	volatile public String aS[];// StringArrayResource
	volatile public byte aB[];// OpaqueResource
	// int arrLength;
	private TreeElementImpl owner;
	int typeKey;
	int footprint;

	public LeafValue(TreeElementImpl owner) {
		this.owner = owner;
		this.typeKey = owner.typeKey;
		this.footprint = 0;
	}

	@Override
	public boolean getBoolean() throws UnsupportedOperationException {
		if (typeKey != DBConstants.TYPE_KEY_BOOLEAN)
			throw new UnsupportedOperationException();
		return Z;
	}

	@Override
	public void setBoolean(boolean z) throws UnsupportedOperationException {
		if (typeKey != DBConstants.TYPE_KEY_BOOLEAN)
			throw new UnsupportedOperationException();
		this.Z = z;
		// inform persistence policy about the change
		if (owner.db.activatePersistence)
			owner.store(ChangeInfo.VALUE_CHANGED);
	}

	@Override
	public float getFloat() throws UnsupportedOperationException {
		if (typeKey != DBConstants.TYPE_KEY_FLOAT)
			throw new UnsupportedOperationException();
		return F;
	}

	@Override
	public void setFloat(float f) throws UnsupportedOperationException {
		if (typeKey != DBConstants.TYPE_KEY_FLOAT)
			throw new UnsupportedOperationException();
		this.F = f;
		// inform persistence policy about the change if the persistence is
		// active
		if (owner.db.activatePersistence)
			owner.store(ChangeInfo.VALUE_CHANGED);
	}

	@Override
	public int getInt() throws UnsupportedOperationException {
		if (typeKey != DBConstants.TYPE_KEY_INT)
			throw new UnsupportedOperationException();
		return I;
	}

	@Override
	public void setInt(int i) throws UnsupportedOperationException {
		if (typeKey != DBConstants.TYPE_KEY_INT)
			throw new UnsupportedOperationException();
		this.I = i;
		// inform persistence policy about the change
		if (owner.db.activatePersistence)
			owner.store(ChangeInfo.VALUE_CHANGED);
	}

	@Override
	public long getLong() throws UnsupportedOperationException {
		if (typeKey != DBConstants.TYPE_KEY_LONG)
			throw new UnsupportedOperationException();
		return J;
	}

	@Override
	public void setLong(long j) throws UnsupportedOperationException {
		if (typeKey != DBConstants.TYPE_KEY_LONG)
			throw new UnsupportedOperationException();
		this.J = j;
		// inform persistence policy about the change
		if (owner.db.activatePersistence)
			owner.store(ChangeInfo.VALUE_CHANGED);
	}

	@Override
	public String getString() throws UnsupportedOperationException {
		if (typeKey != DBConstants.TYPE_KEY_STRING)
			throw new UnsupportedOperationException();
		return S;
	}

	@Override
	public void setString(String s) throws UnsupportedOperationException {
		if (typeKey != DBConstants.TYPE_KEY_STRING)
			throw new UnsupportedOperationException();
		this.S = s;
		// inform persistence policy about the change
		if (owner.db.activatePersistence)
			owner.store(ChangeInfo.VALUE_CHANGED);
	}

	@Override
	public boolean[] getBooleanArr() throws UnsupportedOperationException {
		if (typeKey != DBConstants.TYPE_KEY_BOOLEAN_ARR)
			throw new UnsupportedOperationException();
		return aZ;
	}

	@Override
	public void setBooleanArr(boolean[] aZ) throws UnsupportedOperationException {
		if (typeKey != DBConstants.TYPE_KEY_BOOLEAN_ARR)
			throw new UnsupportedOperationException();
		this.aZ = aZ;
		this.footprint = aZ.length + 4;
		// inform persistence policy about the change
		if (owner.db.activatePersistence)
			owner.store(ChangeInfo.VALUE_CHANGED);
	}

	@Override
	public float[] getFloatArr() throws UnsupportedOperationException {
		if (typeKey != DBConstants.TYPE_KEY_FLOAT_ARR)
			throw new UnsupportedOperationException();
		return aF;
	}

	@Override
	public void setFloatArr(float[] aF) throws UnsupportedOperationException {
		if (typeKey != DBConstants.TYPE_KEY_FLOAT_ARR)
			throw new UnsupportedOperationException();
		this.aF = aF;
		this.footprint = aF.length + 4;
		// inform persistence policy about the change
		if (owner.db.activatePersistence)
			owner.store(ChangeInfo.VALUE_CHANGED);
	}

	@Override
	public int[] getIntArr() throws UnsupportedOperationException {
		if (typeKey != DBConstants.TYPE_KEY_INT_ARR)
			throw new UnsupportedOperationException();
		return aI;
	}

	@Override
	public void setIntArr(int[] aI) throws UnsupportedOperationException {
		if (typeKey != DBConstants.TYPE_KEY_INT_ARR)
			throw new UnsupportedOperationException();
		this.aI = aI;
		this.footprint = aI.length + 4;
		// inform persistence policy about the change
		if (owner.db.activatePersistence)
			owner.store(ChangeInfo.VALUE_CHANGED);
	}

	@Override
	public long[] getLongArr() throws UnsupportedOperationException {
		if (typeKey != DBConstants.TYPE_KEY_LONG_ARR)
			throw new UnsupportedOperationException();
		return aJ;
	}

	@Override
	public void setLongArr(long[] aJ) throws UnsupportedOperationException {
		if (typeKey != DBConstants.TYPE_KEY_LONG_ARR)
			throw new UnsupportedOperationException();
		this.aJ = aJ;
		this.footprint = aJ.length + 4;
		// inform persistence policy about the change
		if (owner.db.activatePersistence)
			owner.store(ChangeInfo.VALUE_CHANGED);
	}

	@Override
	public String[] getStringArr() throws UnsupportedOperationException {
		if (typeKey != DBConstants.TYPE_KEY_STRING_ARR)
			throw new UnsupportedOperationException();
		return aS;
	}

	@Override
	public void setStringArr(String[] aS) throws UnsupportedOperationException {
		if (typeKey != DBConstants.TYPE_KEY_STRING_ARR)
			throw new UnsupportedOperationException();
		this.aS = aS;
		int len = 0;
		for (String str : aS) {
			try {
				len += str.length();
			} catch (NullPointerException e) {
			}
		}
		this.footprint = len + 4;
		// inform persistence policy about the change
		if (owner.db.activatePersistence)
			owner.store(ChangeInfo.VALUE_CHANGED);
	}

	@Override
	public byte[] getByteArr() throws UnsupportedOperationException {
		if (typeKey != DBConstants.TYPE_KEY_OPAQUE)
			throw new UnsupportedOperationException();
		return aB;
	}

	@Override
	public void setByteArr(byte[] aB) throws UnsupportedOperationException {
		if (typeKey != DBConstants.TYPE_KEY_OPAQUE)
			throw new UnsupportedOperationException();
		this.aB = aB;
		this.footprint = aB.length + 4;
		// inform persistence policy about the change
		if (owner.db.activatePersistence)
			owner.store(ChangeInfo.VALUE_CHANGED);
	}

	@Override
	public int getArrayLength() throws UnsupportedOperationException {
		int res = 0;
		switch (typeKey) {
		case DBConstants.TYPE_KEY_OPAQUE:
			res = aB == null ? 0 : aB.length;
			break;
		case DBConstants.TYPE_KEY_INT_ARR:
			res = aI == null ? 0 : aI.length;
			break;
		case DBConstants.TYPE_KEY_FLOAT_ARR:
			res = aF == null ? 0 : aF.length;
			break;
		case DBConstants.TYPE_KEY_BOOLEAN_ARR:
			res = aZ == null ? 0 : aZ.length;
			break;
		case DBConstants.TYPE_KEY_STRING_ARR:
			res = aS == null ? 0 : aS.length;
			break;
		case DBConstants.TYPE_KEY_LONG_ARR:
			res = aJ == null ? 0 : aJ.length;
			break;
		//		case DBConstants.TYPE_KEY_COMPLEX_ARR:
		//			break;
		default:
			throw new UnsupportedOperationException();
		}
		return res;
	}
}
