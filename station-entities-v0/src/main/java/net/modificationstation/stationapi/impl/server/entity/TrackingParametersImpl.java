package net.modificationstation.stationapi.impl.server.entity;

import net.mine_diver.unsafeevents.listener.EventListener;
import net.minecraft.entity.EntityBase;
import net.minecraft.network.EntityHashSet;
import net.minecraft.server.network.ServerEntityTracker;
import net.modificationstation.stationapi.api.StationAPI;
import net.modificationstation.stationapi.api.mod.entrypoint.Entrypoint;
import net.modificationstation.stationapi.api.mod.entrypoint.EventBusPolicy;
import net.modificationstation.stationapi.api.server.entity.CustomTracking;
import net.modificationstation.stationapi.api.server.entity.HasTrackingParameters;
import net.modificationstation.stationapi.api.server.entity.TrackingParametersProvider;
import net.modificationstation.stationapi.api.server.event.entity.TrackEntityEvent;
import net.modificationstation.stationapi.api.util.TriState;

/**
 * {@link TrackingParametersProvider} implementation class.
 * @author mine_diver
 * @see TrackEntityEvent
 * @see CustomTracking
 * @see TrackingParametersProvider
 * @see HasTrackingParameters
 */
@Entrypoint(eventBus = @EventBusPolicy(registerInstance = false))
@EventListener(phase = StationAPI.INTERNAL_PHASE)
public class TrackingParametersImpl {

    /**
     * Handles entity's {@link HasTrackingParameters} annotation if it's present via {@link TrackEntityEvent} hook.
     * @param event the {@link TrackEntityEvent} event.
     * @see TrackEntityEvent
     */
    @EventListener
    private static void trackEntity(TrackEntityEvent event) {
        Class<? extends EntityBase> entityClass = event.entityToTrack.getClass();
        if (entityClass.isAnnotationPresent(HasTrackingParameters.class)) {
            HasTrackingParameters at = entityClass.getAnnotation(HasTrackingParameters.class);
            track(event.entityTracker, event.trackedEntities, event.entityToTrack, at.trackingDistance(), at.updatePeriod(), at.sendVelocity());
        }
    }

    /**
     * Tracking logic implementation.
     * @param entityTracker the dimension's tracker instance. Can be used to (un)track entities.
     * @param trackedEntities the set of tracked entities. Can be used to check if entity is already tracked.
     * @param entityToTrack the entity that server tries to track.
     * @param trackingDistance the distance from the player to the entity in blocks within which the entity should be sent to client (tracked).
     * @param updatePeriod the period in ticks with which the entity updates should be sent to client (position, velocity, etc).
     * @param sendVelocity whether or not should server send velocity updates to clients (fireballs don't send velocity, because client can calculate it itself, and paintings don't have velocity at all).
     */
    public static void track(ServerEntityTracker entityTracker, EntityHashSet trackedEntities, EntityBase entityToTrack, int trackingDistance, int updatePeriod, TriState sendVelocity) {
        if (trackedEntities.containsId(entityToTrack.entityId))
            entityTracker.method_1669(entityToTrack);
        if (sendVelocity == TriState.UNSET)
            entityTracker.method_1666(entityToTrack, trackingDistance, updatePeriod);
        else
            entityTracker.trackEntity(entityToTrack, trackingDistance, updatePeriod, sendVelocity.getBool());
    }
}
