/**
 * Copyright 2013 SyncWise International SARL
 * All rights reserved.
 * 
 * The text, images, audio and video files herein are the copyrighted works of SyncWise International SARL.
 * Reproduction in whole or in part, whether on paper, on the Internet, on CD-ROM, or any other medium,
 * including utilization in machines capable of reproduction or without the express permission of SyncWise
 * International SARL is prohibited.
 * */

/**
 * Copyright 2012 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.SyncWise.Android.Modules.BarcodeIntegration;

import android.content.Intent;
import android.support.v4.app.Fragment;

/**
 * IntentIntegrator for the V4 Android compatibility package.
 * 
 * @author Lachezar Dobrev
 */
public final class IntentIntegratorSupportV4 extends IntentIntegrator {

private final Fragment fragment;

	/**
	 * @param fragment Fragment to handle activity response.
	 */
	public IntentIntegratorSupportV4 ( Fragment fragment ) {
		super ( fragment.getActivity () );
		this.fragment = fragment;
	}
	
	@Override
	protected void startActivityForResult ( Intent intent , int code ) {
		fragment.startActivityForResult ( intent , code );
	}

}