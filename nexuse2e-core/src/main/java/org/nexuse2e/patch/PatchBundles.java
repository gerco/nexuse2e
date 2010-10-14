/**
 *  NEXUSe2e Business Messaging Open Source
 *  Copyright 2000-2009, Tamgroup and X-ioma GmbH
 *
 *  This is free software; you can redistribute it and/or modify it
 *  under the terms of the GNU Lesser General Public License as
 *  published by the Free Software Foundation version 2.1 of
 *  the License.
 *
 *  This software is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this software; if not, write to the Free
 *  Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 *  02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.nexuse2e.patch;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a collection of <code>PatchBundle</code> objects. It is not thread-safe!
 *
 * @author Jonas Reese
 * @version $LastChangedRevision:  $ - $LastChangedDate:  $ by $LastChangedBy:  $
 */
public class PatchBundles {

    private List<PatchBundle> patchBundles;
    
    
    /**
     * Constructs a new, initially empty <code>PatchBundles</code> object.
     */
    public PatchBundles() {
        patchBundles = new ArrayList<PatchBundle>();
    }
    
    /**
     * Adds a <code>PatchBundle</code> to this collection.
     * @param patchBundle The patch bundle to add.
     */
    public void addPatchBundle( PatchBundle patchBundle ) {
        patchBundles.add( patchBundle );
    }
    
    /**
     * Removes a patch bundle from this collection.
     * @param patchBundle The patch bundle to be removed.
     * @return <code>true</code> if the patch bundle has successfully been removed,
     * <code>false</code> otherwise.
     */
    public boolean removePatchBundle( PatchBundle patchBundle ) {
        return patchBundles.remove( patchBundle );
    }
    
    /**
     * Gets a flattened list of all patches that are contained in a child <code>PatchBundle</code>.
     * @return A newly created <code>List</code> of all contained patches.
     */
    public List<Patch> getPatches() {
        List<Patch> result = new ArrayList<Patch>();
        for (PatchBundle pb : patchBundles) {
            result.addAll( pb.getPatches() );
        }
        return result;
    }
    
    /**
     * Gets all patch bundles as a <code>List</code>.
     * @return All patch bundles. Not <code>null</code>.
     */
    public List<PatchBundle> getPatchBundles() {
        return patchBundles;
    }
    
    /**
     * Returns <code>true</code> if and only if this collection is empty.
     * @return The "empty" state.
     */
    public boolean isEmpty() {
        return patchBundles.isEmpty();
    }

    /**
     * Tries to find a patch by it's class name.
     * @param patchClassName The patch class name.
     * @return The patch if found, <code>null</code> otherwise.
     */
    public Patch getPatchByClassName( String patchClassName ) {
        for (PatchBundle bundle : patchBundles) {
            for (Patch patch : bundle.getPatches()) {
                if (patch.getClass().getName().equals( patchClassName )) {
                    return patch;
                }
            }
        }
        return null;
    }
}
