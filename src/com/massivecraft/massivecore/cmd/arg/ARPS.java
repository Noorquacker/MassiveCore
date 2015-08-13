package com.massivecraft.massivecore.cmd.arg;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;

import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.ps.PS;
import com.massivecraft.massivecore.ps.PSBuilder;

public class ARPS extends ARAbstract<PS>
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	private static ARPS i = new ARPS();
	public static ARPS get() { return i; }
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public PS read(String arg, CommandSender sender) throws MassiveException
	{
		// Ellador 34 13 78.6 (standard one)
		// 34 13 79 (standard one)
		// pitch14.5
		// worldEllador,yaw14,
		// x15,y18,z8003
		// x15 y32 z99 wEllador
		// x:15 y:32 z:99 w:Ellador
		
		// We get the sender ps
		PS senderPs = new PSBuilder().build();
		if (sender instanceof Entity) senderPs = PS.valueOf((Entity)sender);
		
		// We remove all commas optionally followed by spaces
		String argInner = arg.replaceAll("\\:\\s*", "");
		
		// We split on comma and space to get the list of raw entries.
		List<String> parts = Arrays.asList(argInner.split("[\\s,]+"));
		
		// Then we test the standard ones
		if (parts.size() == 4)
		{
			try
			{
				String world = ARWorldId.get().read(parts.get(0), sender);
				double locationX = ARDouble.get().read(parts.get(1), sender);
				double locationY = ARDouble.get().read(parts.get(2), sender);
				double locationZ = ARDouble.get().read(parts.get(3), sender);
				return new PSBuilder(senderPs).world(world).locationX(locationX).locationY(locationY).locationZ(locationZ).build();
			}
			catch (Exception e)
			{
				
			}
			
			try
			{
				double locationX = ARDouble.get().read(parts.get(0), sender);
				double locationY = ARDouble.get().read(parts.get(1), sender);
				double locationZ = ARDouble.get().read(parts.get(2), sender);
				String world = ARWorldId.get().read(parts.get(3), sender);
				return new PSBuilder(senderPs).world(world).locationX(locationX).locationY(locationY).locationZ(locationZ).build();
			}
			catch (Exception e)
			{
				
			}
		}
		else if (parts.size() == 3)
		{
			try
			{
				double locationX = ARDouble.get().read(parts.get(0), sender);
				double locationY = ARDouble.get().read(parts.get(1), sender);
				double locationZ = ARDouble.get().read(parts.get(2), sender);
				return new PSBuilder(senderPs).locationX(locationX).locationY(locationY).locationZ(locationZ).build();
			}
			catch (Exception e)
			{
				
			}
		}
		
		// Then we split each entry using known prefixes and append the ps builder.
		PSBuilder ret = new PSBuilder(senderPs);
		boolean something = false;
		for (String part : parts)
		{
			String value;
			
			value = getValue(part, PS.NAME_SERIALIZED_WORLD, PS.NAME_FULL_WORLD);
			if (value != null)
			{
				ret.world(ARWorldId.get().read(value, sender));
				something = true;
			}
			
			value = getValue(part, PS.NAME_SERIALIZED_BLOCKX, PS.NAME_FULL_BLOCKX);
			if (value != null)
			{
				ret.blockX(ARInteger.get().read(value, sender));
				something = true;
			}
			
			value = getValue(part, PS.NAME_SERIALIZED_BLOCKY, PS.NAME_FULL_BLOCKY);
			if (value != null)
			{
				ret.blockY(ARInteger.get().read(value, sender));
				something = true;
			}
			
			value = getValue(part, PS.NAME_SERIALIZED_BLOCKZ, PS.NAME_FULL_BLOCKZ);
			if (value != null)
			{
				ret.blockZ(ARInteger.get().read(value, sender));
				something = true;
			}
			
			value = getValue(part, PS.NAME_SERIALIZED_LOCATIONX, PS.NAME_FULL_LOCATIONX, "x");
			if (value != null)
			{
				ret.locationX(ARDouble.get().read(value, sender));
				something = true;
			}
			
			value = getValue(part, PS.NAME_SERIALIZED_LOCATIONY, PS.NAME_FULL_LOCATIONY, "y");
			if (value != null)
			{
				ret.locationY(ARDouble.get().read(value, sender));
				something = true;
			}
			
			value = getValue(part, PS.NAME_SERIALIZED_LOCATIONZ, PS.NAME_FULL_LOCATIONZ, "z");
			if (value != null)
			{
				ret.locationZ(ARDouble.get().read(value, sender));
				something = true;
			}
			
			value = getValue(part, PS.NAME_SERIALIZED_CHUNKX, PS.NAME_FULL_CHUNKX);
			if (value != null)
			{
				ret.chunkX(ARInteger.get().read(value, sender));
				something = true;
			}
			
			value = getValue(part, PS.NAME_SERIALIZED_CHUNKZ, PS.NAME_FULL_CHUNKZ);
			if (value != null)
			{
				ret.chunkZ(ARInteger.get().read(value, sender));
				something = true;
			}
			
			value = getValue(part, PS.NAME_SERIALIZED_PITCH, PS.NAME_FULL_PITCH);
			if (value != null)
			{
				ret.pitch(ARFloat.get().read(value, sender));
				something = true;
			}
			
			value = getValue(part, PS.NAME_SERIALIZED_YAW, PS.NAME_FULL_YAW);
			if (value != null)
			{
				ret.yaw(ARFloat.get().read(value, sender));
				something = true;
			}
			
			value = getValue(part, PS.NAME_SERIALIZED_VELOCITYX, PS.NAME_FULL_VELOCITYX);
			if (value != null)
			{
				ret.velocityX(ARDouble.get().read(value, sender));
				something = true;
			}
			
			value = getValue(part, PS.NAME_SERIALIZED_VELOCITYY, PS.NAME_FULL_VELOCITYY);
			if (value != null)
			{
				ret.velocityY(ARDouble.get().read(value, sender));
				something = true;
			}
			
			value = getValue(part, PS.NAME_SERIALIZED_VELOCITYZ, PS.NAME_FULL_VELOCITYZ);
			if (value != null)
			{
				ret.velocityZ(ARDouble.get().read(value, sender));
				something = true;
			}
		}
	
		if ( ! something)
		{
			throw new MassiveException().addMsg("<b>Invalid physical state \"<h>%s<b>\".", arg);
		}
		
		return ret.build();
	}

	@Override
	public Collection<String> getTabList(CommandSender sender, String arg)
	{
		return null;
	}
	
	public static String getValue(String entry, String... prefixes)
	{
		for (String prefix : prefixes)
		{
			if ( ! StringUtils.startsWithIgnoreCase(entry, prefix)) continue;
			return entry.substring(prefix.length());
		}
		return null;
	}
	
}
