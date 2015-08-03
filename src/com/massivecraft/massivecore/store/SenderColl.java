package com.massivecraft.massivecore.store;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import com.massivecraft.massivecore.Predictate;
import com.massivecraft.massivecore.SenderPresence;
import com.massivecraft.massivecore.SenderType;
import com.massivecraft.massivecore.cmd.arg.ARSenderEntity;
import com.massivecraft.massivecore.cmd.arg.ARSenderId;
import com.massivecraft.massivecore.util.IdUtil;
import com.massivecraft.massivecore.util.MUtil;

public class SenderColl<E extends SenderEntity<E>> extends Coll<E> implements SenderIdSource
{
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public SenderColl(String name, Class<E> entityClass, Db db, Plugin plugin, boolean creative, boolean lowercasing)
	{	
		super(name, entityClass, db, plugin, creative, lowercasing);
	}
	
	public SenderColl(String name, Class<E> entityClass, Db db, Plugin plugin)
	{	
		super(name, entityClass, db, plugin, true, true);
	}

	// -------------------------------------------- //
	// STACK TRACEABILITY
	// -------------------------------------------- //
	
	@Override
	public void onTick()
	{
		super.onTick();
	}
	
	// -------------------------------------------- //
	// OVERRIDE: Coll
	// -------------------------------------------- //
	
	public static void denyNpc(Object oid)
	{
		if (MUtil.isNpc(oid)) throw new IllegalArgumentException("NPCs are not allowed: " + oid);
	}
	
	@Override
	public E get(Object oid)
	{
		denyNpc(oid);
		return super.get(oid);
	}
	
	@Override
	public E get(Object oid, boolean creative)
	{
		denyNpc(oid);
		return super.get(oid, creative);
	}
	
	@Override
	public String fixId(Object oid)
	{
		// A null oid should always return null.
		if (oid == null) return null;
		
		String ret = null;
		
		if (oid instanceof String)
		{
			ret = (String)oid;
		}
		else if (oid.getClass() == this.entityClass)
		{
			ret = this.entity2id.get(oid);
		}
		
		if (ret == null)
		{
			// Always lower case.
			return IdUtil.getId(oid);
		}
		
		if (this.isLowercasing())
		{
			ret = ret.toLowerCase();
		}
		
		return ret;
	}
	
	// -------------------------------------------- //
	// OVERRIDE: SenderIdSource
	// -------------------------------------------- //
	
	@Override
	public Collection<Collection<String>> getSenderIdCollections()
	{
		List<Collection<String>> ret = new ArrayList<Collection<String>>();
		
		ret.add(this.getIds());
		
		// For creative collections we must add all known ids.
		// You could say the corresponding entities latently exist in the collection because it's creative.
		if (this.isCreative())
		{
			ret.add(IdUtil.getIds(SenderPresence.ANY, SenderType.ANY));
		}
		
		return ret;
	}
	
	// -------------------------------------------- //
	// ARGUMENT READERS
	// -------------------------------------------- //
	
	public ARSenderEntity<E> getAREntity()
	{
		return ARSenderEntity.get(this);
	}
	
	public ARSenderEntity<E> getAREntity(SenderPresence presence)
	{
		return ARSenderEntity.get(this, presence);
	}
	
	public ARSenderId getARId()
	{
		return ARSenderId.get(this);
	}
	
	public ARSenderId getARId(SenderPresence presence)
	{
		return ARSenderId.get(this, presence);
	}
	
	// -------------------------------------------- //
	// GET ALL ONLINE / OFFLINE
	// -------------------------------------------- //
	
	public static final Predictate<SenderEntity<?>> PREDICTATE_ONLINE = new Predictate<SenderEntity<?>>()
	{
		@Override
		public boolean apply(SenderEntity<?> entity)
		{
			return entity.isOnline();
		}
	};
	
	public static final Predictate<SenderEntity<?>> PREDICTATE_OFFLINE = new Predictate<SenderEntity<?>>()
	{
		@Override
		public boolean apply(SenderEntity<?> entity)
		{
			return entity.isOffline();
		}
	};
	
	public Collection<E> getAllOnline()
	{
		return this.getAll(PREDICTATE_ONLINE);
	}
	
	public Collection<E> getAllOffline()
	{
		return this.getAll(PREDICTATE_OFFLINE);
	}
	
	// -------------------------------------------- //
	// SENDER REFERENCE MANAGEMENT
	// -------------------------------------------- //
	
	protected void setSenderReference(String senderId, CommandSender sender)
	{
		 E senderEntity = this.get(senderId, false);
		 if (senderEntity == null) return;
		 senderEntity.sender = sender;
		 senderEntity.senderInitiated = true;
	}
	
	public static void setSenderReferences(String senderId, CommandSender sender)
	{
		for (Coll<?> coll : Coll.getInstances())
		{
			if (!(coll instanceof SenderColl)) continue;
			SenderColl<?> senderColl = (SenderColl<?>)coll;
			senderColl.setSenderReference(senderId, sender);
		}
	}

}
