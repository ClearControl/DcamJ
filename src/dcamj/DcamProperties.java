package dcamj;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.bridj.BridJ;
import org.bridj.FlagSet;
import org.bridj.IntValuedEnum;
import org.bridj.Pointer;
import org.bridj.Pointer.StringType;

import dcamapi.DCAM_PROPERTYATTR;
import dcamapi.DcamapiLibrary;
import dcamapi.DcamapiLibrary.DCAMERR;
import dcamapi.DcamapiLibrary.DCAMIDPROP;
import dcamapi.DcamapiLibrary.DCAMPROPATTRIBUTE;
import dcamapi.DcamapiLibrary.DCAMPROPMODEVALUE;
import dcamapi.DcamapiLibrary.DCAMPROPUNIT;

public class DcamProperties extends DcamBase
{
	private final DcamDevice mDcamDevice;

	private final HashMap<String, DcamProperty> mPropertyMap = new HashMap<String, DcamProperty>();

	public DcamProperties(final DcamDevice pDcamDevice)
	{
		mDcamDevice = pDcamDevice;
		updatePropertyList();
	}

	public final boolean updatePropertyList()
	{
		boolean lSuccess = true;

		@SuppressWarnings(
		{ "unchecked" })
		final Pointer<IntValuedEnum<DcamapiLibrary.DCAMIDPROP>> lPointerToPropertyId = (Pointer) Pointer.allocateCLong();

		while (DcamLibrary.hasSucceeded(DcamapiLibrary.dcampropGetnextid(	mDcamDevice.getHDCAMPointer(),
																																			lPointerToPropertyId,
																																			DcamapiLibrary.DCAMPROPOPTION.DCAMPROP_OPTION_SUPPORT.value)))
		{

			final DcamProperty lDcamProperty = new DcamProperty();

			lDcamProperty.id = lPointerToPropertyId.getCLong();

			{
				final Pointer<Byte> lNameBytes = Pointer.allocateBytes(64);
				final IntValuedEnum<DCAMERR> lError = DcamapiLibrary.dcampropGetname(	mDcamDevice.getHDCAMPointer(),
																																							lPointerToPropertyId.getCLong(),
																																							lNameBytes,
																																							64L);
				final boolean lSuccessGetName = addErrorToListAndCheckHasSucceeded(lError);
				lSuccess &= lSuccessGetName;
				if (!lSuccessGetName)
				{
					break;
				}

				lDcamProperty.name = lNameBytes.getString(StringType.C);
			}

			{
				final DCAM_PROPERTYATTR lDCAM_PROPERTYATTR = new DCAM_PROPERTYATTR();
				lDCAM_PROPERTYATTR.cbSize(BridJ.sizeOf(DCAM_PROPERTYATTR.class));
				lDCAM_PROPERTYATTR.iProp(lPointerToPropertyId.getCLong());

				@SuppressWarnings("unchecked")
				final FlagSet<DCAMERR> lError = (FlagSet<DCAMERR>) DcamapiLibrary.dcampropGetattr(mDcamDevice.getHDCAMPointer(),
																																													Pointer.pointerTo(lDCAM_PROPERTYATTR));
				final boolean lSuccessGetAttribute = true; // always works...
				lSuccess &= lSuccessGetAttribute;
				// System.out.format("name: %s error: %s \n", lDcamProperty.name,
				// lError.toString());

				if (lSuccessGetAttribute)
				{
					final FlagSet<DCAMPROPATTRIBUTE> lFlagSetForAttribute = FlagSet.fromValue(lDCAM_PROPERTYATTR.attribute()
																																																			.value(),
																																										DCAMPROPATTRIBUTE.class);

					final FlagSet<DCAMPROPUNIT> lFlagSetForUnit = FlagSet.fromValue(lDCAM_PROPERTYATTR.iUnit()
																																														.value(),
																																					DCAMPROPUNIT.class);

					lDcamProperty.attribute = lFlagSetForAttribute;
					lDcamProperty.writable = lFlagSetForAttribute.has(DCAMPROPATTRIBUTE.DCAMPROP_ATTR_WRITABLE);

					if (lFlagSetForAttribute.has(DCAMPROPATTRIBUTE.DCAMPROP_TYPE_LONG))
					{
						lDcamProperty.mode = "long";
					}
					else if (lFlagSetForAttribute.has(DCAMPROPATTRIBUTE.DCAMPROP_TYPE_REAL))
					{
						lDcamProperty.mode = "real";
					}
					else if (lFlagSetForAttribute.has(DCAMPROPATTRIBUTE.DCAMPROP_TYPE_MODE))
					{
						lDcamProperty.mode = "mode";
					}

					lDcamProperty.writable = lFlagSetForAttribute.has(DCAMPROPATTRIBUTE.DCAMPROP_ATTR_WRITABLE);
					lDcamProperty.readable = lFlagSetForAttribute.has(DCAMPROPATTRIBUTE.DCAMPROP_ATTR_READABLE);

					final Iterator<DCAMPROPUNIT> lIterator = lFlagSetForUnit.iterator();
					if (lIterator.hasNext())
					{
						lDcamProperty.unit = lIterator.next();
					}
					else
					{
						lDcamProperty.unit = null;
					}

					lDcamProperty.valuemin = lDCAM_PROPERTYATTR.valuemin();
					lDcamProperty.valuemax = lDCAM_PROPERTYATTR.valuemax();
					lDcamProperty.valuestep = lDCAM_PROPERTYATTR.valuestep();
					lDcamProperty.valuedefault = lDCAM_PROPERTYATTR.valuedefault();
				}
			}

			mPropertyMap.put(lDcamProperty.name, lDcamProperty);

		}

		return lSuccess;
	}

	public final Collection<DcamProperty> getPropertyList()
	{
		return mPropertyMap.values();
	}

	public final DcamProperty getProperty(final String pPropertyName)
	{
		return mPropertyMap.get(pPropertyName);
	}

	public final double getPropertyDefaultValue(final String pPropertyName)
	{
		return getProperty(pPropertyName).valuedefault;
	}

	public final double getPropertyMinValue(final String pPropertyName)
	{
		return getProperty(pPropertyName).valuemin;
	}

	public final double getPropertyMaxValue(final String pPropertyName)
	{
		return getProperty(pPropertyName).valuemax;
	}

	public final double getPropertySteps(final String pPropertyName)
	{
		return getProperty(pPropertyName).valuestep;
	}

	public final boolean isPropertyWritable(final String pPropertyName)
	{
		return getProperty(pPropertyName).writable;
	}

	public final boolean isPropertyReadable(final String pPropertyName)
	{
		return getProperty(pPropertyName).readable;
	}

	public final boolean isPropertyReal(final String pPropertyName)
	{
		return getProperty(pPropertyName).mode == "real";
	}

	public final boolean isPropertyLong(final String pPropertyName)
	{
		return getProperty(pPropertyName).mode == "long";
	}

	public double getPropertyValue(final DCAMIDPROP pDCAMIDPROP)
	{
		final Pointer<Double> lPointerToDouble = Pointer.allocateDouble();

		final IntValuedEnum<DCAMERR> lError = DcamapiLibrary.dcampropGetvalue(mDcamDevice.getHDCAMPointer(),
																																					pDCAMIDPROP.value,
																																					lPointerToDouble);
		final boolean lSuccess = addErrorToListAndCheckHasSucceeded(lError);

		if (!lSuccess)
		{
			return Double.NaN;
		}

		final double lValue = lPointerToDouble.getDouble();

		return lValue;
	}

	public final double getPropertyValue(final String pPropertyName)
	{
		final DcamProperty lProperty = getProperty(pPropertyName);

		final Pointer<Double> lPointerToDouble = Pointer.allocateDouble();

		final IntValuedEnum<DCAMERR> lError = DcamapiLibrary.dcampropGetvalue(mDcamDevice.getHDCAMPointer(),
																																					lProperty.id,
																																					lPointerToDouble);
		final boolean lSuccess = addErrorToListAndCheckHasSucceeded(lError);

		if (!lSuccess)
		{
			return Double.NaN;
		}

		final double lValue = lPointerToDouble.getDouble();

		return lValue;
	}

	public final boolean setPropertyValue(final String pPropertyName,
																				final double pValue)
	{
		final DcamProperty lProperty = getProperty(pPropertyName);

		final IntValuedEnum<DCAMERR> lError = DcamapiLibrary.dcampropSetvalue(mDcamDevice.getHDCAMPointer(),
																																					lProperty.id,
																																					pValue);
		final boolean lSuccess = addErrorToListAndCheckHasSucceeded(lError);

		return lSuccess;
	}

	public final boolean setPropertyValue(final DCAMIDPROP pDCAMIDPROP,
																				final double pValue)
	{

		final IntValuedEnum<DCAMERR> lError = DcamapiLibrary.dcampropSetvalue(mDcamDevice.getHDCAMPointer(),
																																					pDCAMIDPROP.value,
																																					pValue);
		final boolean lSuccess = addErrorToListAndCheckHasSucceeded(lError);

		return lSuccess;
	}

	public final boolean setPropertyValue(final DCAMIDPROP pDCAMIDPROP,
																				final DCAMPROPMODEVALUE pDCAMPROPMODEVALUE)
	{

		final IntValuedEnum<DCAMERR> lError = DcamapiLibrary.dcampropSetvalue(mDcamDevice.getHDCAMPointer(),
																																					pDCAMIDPROP.value,
																																					pDCAMPROPMODEVALUE.value);
		final boolean lSuccess = addErrorToListAndCheckHasSucceeded(lError);

		return lSuccess;
	}

	private double setAndGetPropertyValue(final DCAMIDPROP pDCAMIDPROP,
																				final double pValue)
	{
		final Pointer<Double> lPointerToDouble = Pointer.allocateDouble();
		lPointerToDouble.set(pValue);

		final IntValuedEnum<DCAMERR> lError = DcamapiLibrary.dcampropSetgetvalue(	mDcamDevice.getHDCAMPointer(),
																																							pDCAMIDPROP.value,
																																							lPointerToDouble,
																																							0);
		final boolean lSuccess = addErrorToListAndCheckHasSucceeded(lError);

		if (!lSuccess)
		{
			return Double.NaN;
		}

		final double lValue = lPointerToDouble.getDouble();

		return lValue;
	}

	public void listAllProperties()
	{
		for (final Entry<String, DcamProperty> lEntry : mPropertyMap.entrySet())
		{
			final String lName = lEntry.getKey();
			final DcamProperty lDcamProperty = lEntry.getValue();
			System.out.format("property: '%s' \n%s \n",
												lName,
												lDcamProperty.toString());
		}
	}

	public double getExposure()
	{
		return getPropertyValue(DCAMIDPROP.DCAM_IDPROP_EXPOSURETIME);
	}

	public void setExposure(final double pExposure)
	{
		setPropertyValue(DCAMIDPROP.DCAM_IDPROP_EXPOSURETIME, pExposure);
	}

	public double setAndGetExposure(final double pExposure)
	{
		return setAndGetPropertyValue(DCAMIDPROP.DCAM_IDPROP_EXPOSURETIME,
																	pExposure);
	}

	public void setCenteredROI(final int pWidth, final int pHeight)
	{
		final int hpos = 1024 - pWidth / 2;
		final int vpos = 1024 - pHeight / 2;
		setPropertyValue(DCAMIDPROP.DCAM_IDPROP_SUBARRAYHPOS, hpos);
		setPropertyValue(DCAMIDPROP.DCAM_IDPROP_SUBARRAYVPOS, vpos);
		setPropertyValue(DCAMIDPROP.DCAM_IDPROP_SUBARRAYHSIZE, pWidth);
		setPropertyValue(DCAMIDPROP.DCAM_IDPROP_SUBARRAYVSIZE, pHeight);

		setPropertyValue(DCAMIDPROP.DCAM_IDPROP_SUBARRAYMODE, 2);
	}

	public void setInternalTrigger()
	{
		setPropertyValue(	DCAMIDPROP.DCAM_IDPROP_TRIGGERSOURCE,
											DCAMPROPMODEVALUE.DCAMPROP_TRIGGERSOURCE__INTERNAL);
	}

	public void setExternalTrigger()
	{
		setPropertyValue(	DCAMIDPROP.DCAM_IDPROP_TRIGGERSOURCE,
											DCAMPROPMODEVALUE.DCAMPROP_TRIGGERSOURCE__EXTERNAL);
	}

	public void setSoftwareTrigger()
	{
		setPropertyValue(	DCAMIDPROP.DCAM_IDPROP_TRIGGERSOURCE,
											DCAMPROPMODEVALUE.DCAMPROP_TRIGGERSOURCE__SOFTWARE);
	}
}
